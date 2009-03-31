package net.hillsdon.eclipse.terminator.view;

import junit.framework.TestCase;

/**
 * Test for {@link TerminatorEmbedding}.
 * 
 * @author mth
 */
public class TestTerminatorEmbedding extends TestCase {

  public void testMungesPidOutOfProcessList() throws Exception {
    assertEquals("/proc/123/cwd", TerminatorEmbedding.getCwdSymlinkForFirstChildProcess("bash (123) vim (456)")); 
  }
  
}
