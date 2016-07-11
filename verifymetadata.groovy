System.out.println("Check for proper metadata.rb/metadata.json tags");

import groovy.io.FileType
import java.util.regex.*
//Pattern version_pattern    = Pattern.compile("(?!\\.)(\\d+(\\.\\d+)+)(?![\\d\\.])");
//Pattern metadataver_pattern   = Pattern.compile("(^(version)\\s+.(?!\\.)(\\d+(\\.\\d+)+)(?![\\d\\.]).)");

def list = []

def dir = new File("testing-jenkins/.git/refs/tags")
dir.eachFileRecurse (FileType.FILES) { file ->
  list << file
}

def metadata_regex = ~/(^(version)\s+.(?!\.)(\d+(\.\d+)+)(?![\d\.]).)/
def versions = []
new File('testing-jenkins/metadata.rb').eachLine { line ->
        def matcher_meta = metadata_regex.matcher(line)
        while (matcher_meta.find()) {
          versions << matcher_meta.group(3)
        }
}

System.out.println("Found the following versions in metadata: " + versions );

def version_regex = ~/${versions[0]}/
list.each {
  def matcher_version = version_regex.matcher(it.name)
  boolean matchFound = matcher_version.find();
  println it.name
  if (matchFound) {
    System.out.println("found version number: " + it.name);
    System.exit(0);
    }
}

System.out.println("Cannot find valid version");
System.exit(1);
