package net.ossindex.version.impl;

import java.util.ArrayList;
import java.util.List;

public class PostscriptPattern
{
  List<Segment> segments = new ArrayList<>();

  public void pushNumericSegment(final String text) {
    segments.add(new NumericSegment(text));
  }

  public void pushCharacterSegment(final String text) {
    segments.add(new CharacterSegment(text));
  }

  public void pushSeparator(final String text) {
    segments.add(new Separator(text));
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(Segment segment: segments) {
      sb.append(segment.toString());
    }
    return sb.toString();
  }

  public boolean matches(final PostscriptPattern o) {
    if (segments.size() == o.segments.size()) {
      String mySegments = textSegments();
      String yourSegments = o.textSegments();
      if(mySegments.equals(yourSegments)) {
        return true;
      }
    }

    return false;
  }

  private String textSegments() {
    StringBuilder sb = new StringBuilder();
    for (Segment segment: segments) {
      if (segment instanceof CharacterSegment) {
        sb.append(segment.toString()).append(",");
      }
    }
    return sb.toString();
  }

  interface Segment {

  }
  class NumericSegment implements Segment {
    int number;

    public NumericSegment(String text) {
      number = Integer.parseInt(text);
    }

    public String toString() {
      return Integer.toString(number);
    }
  }

  class CharacterSegment implements Segment {
    String text;

    public CharacterSegment(String text) {
      this.text = text;
    }

    public String toString() {
      return text;
    }
  }

  class Separator implements Segment {
    String text;

    public Separator(String text) {
      this.text = text;
    }

    public String toString() {
      return text;
    }
  }
}
