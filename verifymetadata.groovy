System.out.println("Check for proper metadata.rb/metadata.json tags");

import groovy.io.FileType
import java.util.regex.*
Pattern pattern    = Pattern.compile("(?!\\.)(\\d+(\\.\\d+)+)(?![\\d\\.])");

def list = []

def dir = new File("testing-jenkins/.git/refs/tags")
dir.eachFileRecurse (FileType.FILES) { file ->
  list << file
}

list.each {
  Matcher matcher    = pattern.matcher(it.path);
  boolean matchFound = matcher.find();
  if (matchFound) {
    String version = matcher.group(1);
    new File( 'testing-jenkins/metadata.rb' ).text.tokenize( '\n' ).findAll {
      it.matches "^(version\\s+.${version}.)"
    }.each {
    System.out.println("found version number: " + version);
    }
  }
  else {
    System.out.println("No valid versions found, please commit metadata.rb and tags that match");
    System.exit(1)
  }
}
