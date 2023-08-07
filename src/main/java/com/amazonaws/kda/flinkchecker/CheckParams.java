package com.amazonaws.kda.flinkchecker;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

public class CheckParams {
    public MavenProject project;
    public List<Dependency> dependencies;
    public Log log;

    public CheckParams(Log log, MavenProject project, List<Dependency> deps) {
       this.project = project;
       this.dependencies = deps;
       this.log = log;
    }
}
