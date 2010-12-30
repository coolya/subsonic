/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.controller;

import java.awt.Dimension;

import junit.framework.TestCase;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class StreamControllerTestCase extends TestCase {

    public void testGetSuitableVideoSize() {

        // 4:3 aspect rate
        doTestGetSuitableVideoSize(720, 540, 200, 320, 240);
        doTestGetSuitableVideoSize(720, 540, 300, 320, 240);
        doTestGetSuitableVideoSize(720, 540, 400, 320, 240);
        doTestGetSuitableVideoSize(720, 540, 500, 320, 240);
        doTestGetSuitableVideoSize(720, 540, 600, 320, 240);

        doTestGetSuitableVideoSize(720, 540, 700, 480, 360);
        doTestGetSuitableVideoSize(720, 540, 800, 480, 360);
        doTestGetSuitableVideoSize(720, 540, 900, 480, 360);
        doTestGetSuitableVideoSize(720, 540, 1000, 480, 360);

        doTestGetSuitableVideoSize(720, 540, 1100, 640, 480);
        doTestGetSuitableVideoSize(720, 540, 1200, 640, 480);
        doTestGetSuitableVideoSize(720, 540, 1500, 640, 480);
        doTestGetSuitableVideoSize(720, 540, 2000, 640, 480);

        // 16:9 aspect rate
        doTestGetSuitableVideoSize(960, 540, 200, 427, 240);
        doTestGetSuitableVideoSize(960, 540, 300, 427, 240);
        doTestGetSuitableVideoSize(960, 540, 400, 427, 240);
        doTestGetSuitableVideoSize(960, 540, 500, 427, 240);
        doTestGetSuitableVideoSize(960, 540, 600, 427, 240);

        doTestGetSuitableVideoSize(960, 540, 700, 640, 360);
        doTestGetSuitableVideoSize(960, 540, 800, 640, 360);
        doTestGetSuitableVideoSize(960, 540, 900, 640, 360);
        doTestGetSuitableVideoSize(960, 540, 1000, 640, 360);

        doTestGetSuitableVideoSize(960, 540, 1100, 853, 480);
        doTestGetSuitableVideoSize(960, 540, 1200, 853, 480);
        doTestGetSuitableVideoSize(960, 540, 1500, 853, 480);
        doTestGetSuitableVideoSize(960, 540, 2000, 853, 480);

        // Small original size.
        doTestGetSuitableVideoSize(100, 100, 1000, 100, 100);
        doTestGetSuitableVideoSize(100, 1000, 1000, 100, 1000);
        doTestGetSuitableVideoSize(1000, 100, 100, 1000, 100);

        // Unknown original size.
        doTestGetSuitableVideoSize(720, null, 200, 320, 240);
        doTestGetSuitableVideoSize(null, 540, 300, 320, 240);
        doTestGetSuitableVideoSize(null, null, 400, 320, 240);
        doTestGetSuitableVideoSize(720, null, 500, 320, 240);
        doTestGetSuitableVideoSize(null, 540, 600, 320, 240);
        doTestGetSuitableVideoSize(null, null, 700, 480, 360);
        doTestGetSuitableVideoSize(720, null, 1200, 640, 480);
        doTestGetSuitableVideoSize(null, 540, 1500, 640, 480);
        doTestGetSuitableVideoSize(null, null, 2000, 640, 480);
    }

    private void doTestGetSuitableVideoSize(Integer existingWidth, Integer existingHeight, Integer maxBitRate, int expectedWidth, int expectedHeight) {
        StreamController controller = new StreamController();
        Dimension dimension = controller.getSuitableVideoSize(existingWidth, existingHeight, maxBitRate);
        assertEquals("Wrong width.", expectedWidth, dimension.width);
        assertEquals("Wrong height.", expectedHeight, dimension.height);
    }
}
