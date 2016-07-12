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
import hudson.EnvVars

def workspace_dir = manager.build.getEnvVars()["WORKSPACE"];
//def workspace_dir = '/home/tehlers/groovy/testing-jenkins/';

/**
 * @return current jenkins job 
 */ 
def jenkinsJob() {
	def threadName = Thread.currentThread().getName()
	def pattern = Pattern.compile("job/(.*)/build")
	def matcher = pattern.matcher(threadName); matcher.find()
	def jobName = matcher.group(1)
	def jenkinsJob = Hudson.instance.getJob(jobName)
}

/**
 * @return repository tag list using default git on os path 
 */ 
def tagList(dir) {
        def command = [ "/bin/bash", "-c", "cd '${dir}' ; git fetch --tags &> /dev/null ; git tag -l" ]
        def process = command.execute(); process.waitFor()
        def list = process.in.text.tokenize("\n")
}

System.out.println("Check for proper metadata.rb/metadata.json tags");


def metadata_regex = ~/(^(version)\s+.(?!\.)(\d+(\.\d+)+)(?![\d\.]).)/
def versions = []
new File( workspace_dir + '/metadata.rb' ).eachLine { line ->
        def matcher_meta = metadata_regex.matcher(line)
        while (matcher_meta.find()) {
          versions << matcher_meta.group(3)
        }
}

System.out.println("Found the following versions in metadata: " + versions );

try {
  println "Trying to find version";
  def tagList = tagList( workspace_dir )
  def version_regex = ~/${versions[0]}/
  tagList.each {
    def matcher_version = version_regex.matcher(it)
    boolean matchFound = matcher_version.find();
    //println it.name
    if (matchFound) {
      System.out.println("found version number: " + it);
      System.exit(0);
      }
  }

} catch ( e ) {
  [ e.toString() ]

}



System.out.println("Cannot find valid version in your commit, please update metadata.rb and tag the repo");
System.exit(1);
