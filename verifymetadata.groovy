/*** BEGIN META {
  "name" : "Chef metadata verify",
  "comment" : "Verifies a chef cookbook has valid tagging in its metadata.rb",
  "parameters" : [],
  "core": "1.500",
  "authors" : [
    { name : "Timothy Ehlers" }
  ]
} END META**/

import groovy.io.FileType
import java.util.regex.*
import hudson.model.*

def build = Thread.currentThread().executable;
def workspace_dir = build.workspace.toString();

//def workspace_dir = '/home/tehlers/groovy/testing-jenkins/';

/**
 * @return repository tag list using default git on os path 
 */ 
def tagList(dir) {
        def command = [ "/bin/bash", "-c", "cd '${dir}' ; git fetch --tags &> /dev/null ; git tag -l" ]
        def process = command.execute(); process.waitFor()
        def list = process.in.text.tokenize("\n")
}

println("Check for proper metadata.rb/metadata.json tags");


def metadata_regex = ~/(^(version)\s+.(?!\.)(\d+(\.\d+)+)(?![\d\.]).)/
def versions = []
new File( workspace_dir + '/metadata.rb' ).eachLine { line ->
        def matcher_meta = metadata_regex.matcher(line)
        while (matcher_meta.find()) {
          versions << matcher_meta.group(3)
        }
}

println("Found the following versions in metadata: " + versions );


def tagList = tagList( workspace_dir );
def version_regex = ~/${versions[0]}/;
println "Trying to find version";
println "Found versions: " + tagList;
def test = tagList.any { it =~ version_regex };
  
if (!test) {
  println "\n";
  throw new hudson.AbortException("Cannot find valid version in your commit, please update metadata.rb and tag the repo\n");
  println "\n";
}


