System.out.println("Check for proper metadata.rb/metadata.json tags");

import groovy.io.FileType
import java.util.regex.*
import hudson.model.*

def tagList(dir) {
        def command = [ "/bin/bash", "-c", "cd '${dir}' ; git fetch --tags &> /dev/null ; git tag -l" ]
        def process = command.execute(); process.waitFor()
        def list = process.in.text.tokenize("\n")
}


def metadata_regex = ~/(^(version)\s+.(?!\.)(\d+(\.\d+)+)(?![\d\.]).)/
def versions = []
new File( jenkinsJob().workspace + '/metadata.rb').eachLine { line ->
        def matcher_meta = metadata_regex.matcher(line)
        while (matcher_meta.find()) {
          versions << matcher_meta.group(3)
        }
}

System.out.println("Found the following versions in metadata: " + versions );

try {
  println "Trying to find version";
  def tagList = tagList( jenkinsJob().workspace )
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
