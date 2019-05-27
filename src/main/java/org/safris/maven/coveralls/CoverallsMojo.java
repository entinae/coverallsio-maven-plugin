/* Copyright (c) 2019 Seva Safris
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.safris.maven.coveralls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eluder.coveralls.maven.plugin.CoverageParser;
import org.eluder.coveralls.maven.plugin.CoverallsReportMojo;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.httpclient.CoverallsClient;
import org.eluder.coveralls.maven.plugin.json.JsonWriter;
import org.eluder.coveralls.maven.plugin.logging.Logger;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;

@Mojo(name="report", threadSafe=false)
public class CoverallsMojo extends CoverallsReportMojo {
  private static final ReverseExecutor reverseExecutor = new ReverseExecutor();

  @Parameter(defaultValue="${detectGeneratedSourcePaths}")
  private boolean detectGeneratedSourcePaths;

  @Parameter(defaultValue="${aggregateOnly}")
  private boolean aggregateOnly;

  @Parameter(defaultValue="${skipAggregate}")
  private boolean skipAggregate;

  private boolean firstExecution = true;
  private boolean skipExecution = false;
  private boolean wasDryRun = false;

  static Model getModelArtifact(final File pomFile)  {
    try {
      final MavenXpp3Reader reader = new MavenXpp3Reader();
      final Model model = reader.read(new FileReader(pomFile));
      model.setPomFile(pomFile);
      return model;
    }
    catch (final IOException | XmlPullParserException e) {
      throw new IllegalStateException(e);
    }
  }

  private void addGeneratedSourcePaths(final Model model, final List<File> filePaths) throws IOException {
    if ("pom".equals(model.getPackaging())) {
      for (final String module : model.getModules()) {
        addGeneratedSourcePaths(getModelArtifact(new File(model.getPomFile().getParentFile(), module + "/pom.xml")), filePaths);
      }
    }
    else {
      addGeneratedSourcePaths(new File(model.getPomFile().getParentFile(), "/target"), filePaths);
    }
  }

  private void addGeneratedSourcePaths(final File buildDir, final List<File> filePaths) throws IOException {
    final File generatedSources = new File(buildDir, "generated-sources");
    if (!generatedSources.exists())
      return;

    final List<String> paths = new ArrayList<>();
    Files.walk(generatedSources.toPath()).filter(Files::isRegularFile).forEach((o) -> {
      final File file = o.toFile();
      if (!file.getName().endsWith(".java"))
        return;

      final String filePath = file.getParentFile().getAbsolutePath();
      for (final String path : paths)
        if (filePath.startsWith(path))
          return;

      boolean inBlockQuote = false;
      String packageName = null;
      try (final Scanner scanner = new Scanner(file)) {
        scanner.useDelimiter("\r|\n");
        while (scanner.hasNext()) {
          final String line = scanner.next().trim();
          if (inBlockQuote) {
            // Matches a line that has a closing block comment "*/" sequence
            if (line.matches("^(([^*]|(\\*[^/]))*\\*+/([^/]|(/[^*])|(/$))*)*$"))
              inBlockQuote = false;
            else
              continue;
          }

          if (line.length() == 0 || line.startsWith("//")) {
            continue;
          }

          // Matches a line that has an opening block comment "/*" sequence
          if (line.matches("^(([^/]|(/[^*]))*/+\\*([^*]|(\\*[^/])|(\\*$))*)*$")) {
            inBlockQuote = true;
            continue;
          }

          if (line.startsWith("package ")) {
            packageName = line.substring(8, line.indexOf(';'));
            break;
          }

          if (line.contains("class ") || line.contains("interface ") || line.contains("@interface ") || line.contains("enum ")) {
            break;
          }
        }
      }
      catch (final FileNotFoundException e) {
        throw new IllegalStateException(e);
      }

      if (packageName != null)
        paths.add(filePath.substring(0, filePath.length() - packageName.length() - 1));
      else
        log.warn("Could not determine package name of: " + file.getAbsolutePath());
    });

    if (paths.size() == 0)
      return;

    for (final String path : paths)
      filePaths.add(new File(path));
  }

  public boolean isAggregator() {
    return "pom".equalsIgnoreCase(project.getPackaging());
  }

  private List<File> getJacocoReports() {
    final List<String> modules = project.getModules();
    final List<File> reportFiles = new ArrayList<>(modules.size());
    for (final String module : modules) {
      final File moduleDir = new File(project.getBasedir(), module);
      File reportFile = new File(moduleDir, "target/site/jacoco/jacoco.xml");
      if (!reportFile.exists())
        reportFile = new File(moduleDir, "target/jacoco/jacoco.xml");

      if (reportFile.exists())
        reportFiles.add(reportFile);
    }

    return reportFiles;
  }

  private void submitExecution(final MavenProject project, final ReverseExecutor reverseExecutor) {
    log.info("Submitting " + project.getName() + " " + project.getVersion());
    reverseExecutor.submit(project, () -> {
      if (!isAggregator())
        return;

      log.info("Running " + project.getName() + " " + project.getVersion());
      if (skipAggregate) {
        log.info("\"skipAggregate\" property set, skipping aggregate execution for POM module");
        return;
      }

      dryRun = wasDryRun;
      skipExecution = false;
      firstExecution = false;
      try {
        execute();
      }
      catch (final MojoExecutionException | MojoFailureException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  private Log log;

  @Override
  protected Job createJob() throws ProcessingException, IOException {
    if (firstExecution) {
      if (this.log == null)
        this.log = getLog();

      setLog(new FilterLog(this.log) {
        @Override
        public boolean isInfoEnabled() {
          return !aggregateOnly || !firstExecution;
        }

        @Override
        public void info(final CharSequence content) {
          if (isInfoEnabled())
            super.info(content);
        }

        @Override
        public void info(final CharSequence content, final Throwable error) {
          if (isInfoEnabled())
            super.info(content, error);
        }

        @Override
        public void info(final Throwable error) {
          if (isInfoEnabled())
            super.info(error);
        }
      });

      if (aggregateOnly || isAggregator()) {
        skipExecution = true;
        if (!isAggregator())
          log.info("\"aggregateOnly\" property set, skipping plugin execution for non-POM module");
      }

      submitExecution(project, reverseExecutor);
      wasDryRun = dryRun;
      dryRun = true;
    }
    else {
      jacocoReports = getJacocoReports();
    }

    if (detectGeneratedSourcePaths) {
      final List<File> generatedSourceDirs = new ArrayList<>();
      addGeneratedSourcePaths(project.getModel(), generatedSourceDirs);
      if (sourceDirectories != null)
        sourceDirectories.addAll(generatedSourceDirs);
      else
        sourceDirectories = generatedSourceDirs;
    }

    return super.createJob();
  }

  @Override
  protected SourceLoader createSourceLoader(final Job job) {
    return skipExecution ? null : super.createSourceLoader(job);
  }

  @Override
  protected List<CoverageParser> createCoverageParsers(final SourceLoader sourceLoader) throws IOException {
    return skipExecution ? null : super.createCoverageParsers(sourceLoader);
  }

  @Override
  protected CoverallsClient createCoverallsClient() {
    return skipExecution ? null : super.createCoverallsClient();
  }

  @Override
  protected SourceCallback createSourceCallbackChain(final JsonWriter writer, final List<Logger> reporters) {
    return skipExecution ? null : super.createSourceCallbackChain(writer, reporters);
  }

  @Override
  protected void writeCoveralls(final JsonWriter writer, final SourceCallback sourceCallback, final List<CoverageParser> parsers) throws ProcessingException, IOException {
    if (!skipExecution)
      super.writeCoveralls(writer, sourceCallback, parsers);
  }
}