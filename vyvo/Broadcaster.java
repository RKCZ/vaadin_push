package vyvo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.vaadin.flow.shared.Registration;

public class Broadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();

    static Map<Long, Map<String, LinkedList<Consumer<Object>>>> listeners = new HashMap<>();

    public static synchronized Registration register(Long id, String target, Consumer<Object> listener) {
        if (listeners.containsKey(id)) {
        	Map<String, LinkedList<Consumer<Object>>> map = listeners.get(id);
        	if (map.containsKey(target)) {
    			map.get(target).add(listener);
			} else {
				map.put(target, new LinkedList<>());
				map.get(target).add(listener);
			}
		} else {
			listeners.put(id, new HashMap<>());
			listeners.get(id).put(target, new LinkedList<>());
			listeners.get(id).get(target).add(listener);
		}

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.get(id).get(target).remove(listener);
            }
        };
    }

    public static synchronized void broadcast(Long id, String target, Object change) {
        for (Consumer<Object> listener : listeners.get(id).get(target)) {
            executor.execute(() -> {
            	listener.accept(change);
            });
        }
    }
}