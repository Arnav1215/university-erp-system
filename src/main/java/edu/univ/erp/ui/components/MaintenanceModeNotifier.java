package edu.univ.erp.ui.components;
import java.util.concurrent.CopyOnWriteArrayList;
public final class MaintenanceModeNotifier {
    private static final CopyOnWriteArrayList<Runnable> LISTENERS = new CopyOnWriteArrayList<>();

    private MaintenanceModeNotifier() {
    }

    public static void addListener(Runnable listener) {
        if (listener != null) {
            LISTENERS.addIfAbsent(listener);
        }
    }

    public static void removeListener(Runnable listener) {
        if (listener != null) {
            LISTENERS.remove(listener);
        }
    }

    public static void notifyListeners() {
        for (Runnable listener : LISTENERS) {
            try {
                listener.run();
            } catch (Exception ignored) {
                
            }
        }
    }
}

