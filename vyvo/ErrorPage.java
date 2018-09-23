/**
 * 
 */
package vyvo;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * @author kaliv
 *
 */
@Route
public class ErrorPage extends VerticalLayout {
	
	public ErrorPage() {
		Text message = new Text("Sorry page with given parameter was not found. You might try over The Hill or across The Water.");
		
		add(message);
	}

}
