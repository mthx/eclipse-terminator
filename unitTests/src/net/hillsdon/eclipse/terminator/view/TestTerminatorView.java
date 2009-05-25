package net.hillsdon.eclipse.terminator.view;

import static net.hillsdon.eclipse.terminator.view.TerminatorView.createSecondaryId;
import static net.hillsdon.eclipse.terminator.view.TerminatorView.getWorkingDirectoryFromViewId;
import junit.framework.TestCase;

public class TestTerminatorView extends TestCase {

  public void testIdStoresWorkingDirectory() throws Exception {
    assertNull(getWorkingDirectoryFromViewId(null));
    assertNull(getWorkingDirectoryFromViewId(TerminatorView.ID));
    
    assertEquals("/tmp", getWorkingDirectoryFromViewId(createSecondaryId("/tmp")));
    String windowsSecondaryId = createSecondaryId("c:\\Program Files\\Wibble");
    // Apparently this is a no-no so we have to encode.
    assertFalse(windowsSecondaryId.contains(":"));
    assertEquals("c:\\Program Files\\Wibble", getWorkingDirectoryFromViewId(windowsSecondaryId));
  }
  
}
