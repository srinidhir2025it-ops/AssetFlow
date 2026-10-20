
package com.assetflow;

import com.assetflow.ui.AssetFlowUI;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AssetFlowUI());
    }
}
