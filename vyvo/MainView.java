/**
 * 
 */
package vyvo;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

/**
 * @author kaliv
 *
 */
@Push
@Route
public class MainView extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver {
	Broadcaster broadcaster;
	Registration nameRegistration, lastNameRegistration, ageRegistration, sexRegistration, noteRegistration,
			gridRegistration;

	List<Model> list = new LinkedList<>();

	TextField name = new TextField("your name", "Tell us your first name...");
	TextField lastName = new TextField("your last name", "Tell us your last name...");
	DatePicker age = new DatePicker("Select your birth date...");
	ComboBox<Sex> sex = new ComboBox<>("Share your sexual identification with us", Sex.values());
	TextArea note = new TextArea("Notes", "...");
	Grid<Model> grid = new Grid<>(Model.class);
	Button button = new Button("add");

	private Model instance;
	private Text headLine;
	private ListDataProvider<Model> gridProvider;

	// Creating the UI shown separately

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		nameRegistration = Broadcaster.register(instance.getId(), "name", value -> {
			ui.access(() -> name.setValue((String) value));
		});
		lastNameRegistration = Broadcaster.register(instance.getId(), "lastName", value -> {
			ui.access(() -> lastName.setValue((String) value));
		});
		ageRegistration = Broadcaster.register(instance.getId(), "age", value -> {
			if (value != null) {
				ui.access(() -> age.setValue((LocalDate) value));
			} else {
				ui.access(() -> age.setValue(null));
			}
		});
		sexRegistration = Broadcaster.register(instance.getId(), "sex", value -> {
			if (value != null) {
				ui.access(() -> sex.setValue(Sex.valueOf((String) value)));
			} else {
				ui.access(() -> sex.setValue(null));
			}

		});
		noteRegistration = Broadcaster.register(instance.getId(), "note", value -> {
			ui.access(() -> note.setValue((String) value));
		});
		gridRegistration = Broadcaster.register(instance.getId(), "grid", value -> {
			ui.access(() -> grid.setDataProvider((DataProvider<Model, ?>) value));
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		nameRegistration.remove();
		lastNameRegistration.remove();
		ageRegistration.remove();
		sexRegistration.remove();
		noteRegistration.remove();
		nameRegistration = null;
		lastNameRegistration = null;
		ageRegistration = null;
		sexRegistration = null;
		noteRegistration = null;
	}

	public MainView() {
		this.instance = new Model();
		broadcaster = new Broadcaster();
		Binder<Model> binder = new Binder<>(Model.class);
		binder.setBean(instance);
		binder.forField(name).asRequired().bind("name");
		binder.forField(lastName).asRequired().bind("lastName");
		binder.forField(sex).bind("sex");
		binder.forField(age).bind("age");
		binder.forField(note).bind("note");

		note.setValueChangeMode(ValueChangeMode.EAGER);
		name.setValueChangeMode(ValueChangeMode.ON_BLUR);
		lastName.setValueChangeMode(ValueChangeMode.ON_BLUR);

		headLine = new Text("placeholder");

		gridProvider = DataProvider.ofCollection(list);
		button.addClickListener(e -> {
			list.add(instance);
			/* FIXME IllegalStateException is thrown approx. on third execution */ 
			synchronized (this) {
				gridProvider.refreshAll();
			}
		});
		grid.setDataProvider(gridProvider);
		this.setSizeFull();
		grid.setSizeFull();

		add(headLine, name, lastName, age, sex, note, button, grid);
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		try {
			this.instance.setId(Long.parseLong(parameter));
			headLine.setText(parameter);
		} catch (NumberFormatException e) {
			event.rerouteTo(ErrorPage.class);
		}
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		name.addValueChangeListener(e -> Broadcaster.broadcast(instance.getId(), "name", name.getValue()));
		lastName.addValueChangeListener(e -> Broadcaster.broadcast(instance.getId(), "lastName", lastName.getValue()));
		age.addValueChangeListener(e -> {
			if (age.getValue() != null) {
				Broadcaster.broadcast(instance.getId(), "age", age.getValue());
			} else {
				Broadcaster.broadcast(instance.getId(), "age", null);
			}
		});
		sex.addValueChangeListener(e -> {
			if (sex.getValue() != null) {
				Broadcaster.broadcast(instance.getId(), "sex", sex.getValue().toString());
			} else {
				Broadcaster.broadcast(instance.getId(), "sex", null);
			}
		});
		note.addValueChangeListener(e -> Broadcaster.broadcast(instance.getId(), "note", note.getValue()));
		gridProvider
				.addDataProviderListener(e -> {
					Broadcaster.broadcast(instance.getId(), "grid", gridProvider);
				});
	}
}
