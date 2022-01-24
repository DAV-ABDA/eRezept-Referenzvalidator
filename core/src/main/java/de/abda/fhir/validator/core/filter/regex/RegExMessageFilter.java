package de.abda.fhir.validator.core.filter.regex;

import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.filter.MessageFilter;

/**
 * Stehlt zur Verfügung die Liste mit FilterBeschreibungen aus XML File.
 * 
 * @author Dzmitry Liashenka
 * @author Georg Tsakumagos
 *
 */
public class RegExMessageFilter implements MessageFilter {
	private final static Logger LOGGER = Logger.getLogger(RegExMessageFilter.class.getName());

	private static final String ERROR_URL_NULL = "Die übergebene URL darf nicht NULL sein.";
	private static final String ERROR_MESSAGES_NULL = "Die übergebene Liste mit SingleValidationMessages darf nicht NULL sein.";
	private static final String ERROR_CANT_READ_FILE = "Fehler beim Einlesen der FilterBeschreibungsListe aus der Quelle: '%01$s'.";
	private static final String ERROR_CREATE_MARSHALLER_UNMARSHALLER = "Fehler beim Erstellen Unmarshaller.";
	private static final String DEBUG_MESSAGE_FILTERED = "'%01$s' message wird gefiltert.";
	private static final String ERROR_WRITER_NULL = "Die übergebene Writer darf nicht NULL sein.";
	private static final String ERROR_CANT_WRITE_FILE = "Fehler beim Schreiben der FilterBeschreibungsListe.";

	private final List<FilterBeschreibung> filterBeschreibungsListe;

	/**
	 * Default Konstruktor
	 */
	public RegExMessageFilter() throws IllegalArgumentException, RuntimeException {
		this.filterBeschreibungsListe = Collections.unmodifiableList(Collections.emptyList());
	}

	/**
	 * Konstruktor
	 * 
	 * @param url Pfad zum XML File mit {@linkplain FilterBeschreibung}.
	 * @throws IllegalArgumentException Wenn übergebene Parameter <code>null</code>
	 *                                  ist.
	 * @throws RuntimeException         Wenn beim Lesen der Daten ein Fehler
	 *                                  auftritt.
	 */
	public RegExMessageFilter(URL url) throws IllegalArgumentException, RuntimeException {
		if (null == url) {
			throw new IllegalArgumentException(ERROR_URL_NULL);
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FilterBeschreibungsListe.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			try (InputStream is = url.openStream()) {
				FilterBeschreibungsListe result = jaxbUnmarshaller
						.unmarshal(new StreamSource(is), FilterBeschreibungsListe.class).getValue();

				if ( null == result.getBeschreibungFilterList()) {
					this.filterBeschreibungsListe = Collections.unmodifiableList(Collections.emptyList());
				} else {
					this.filterBeschreibungsListe = Collections.unmodifiableList(result.getBeschreibungFilterList());
				}

			} catch (Exception e) {
				throw new RuntimeException(String.format(ERROR_CANT_READ_FILE, url), e);
			}

		} catch (final Throwable e) {
			throw new RuntimeException(ERROR_CREATE_MARSHALLER_UNMARSHALLER, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filter(List<SingleValidationMessage> messages) throws IllegalArgumentException {
		if (null == messages) {
			throw new IllegalArgumentException(ERROR_MESSAGES_NULL);
		}

		for (FilterBeschreibung fb : this.filterBeschreibungsListe) {
			final Iterator<SingleValidationMessage> iterator = messages.iterator();

			while (iterator.hasNext()) {
				SingleValidationMessage next = iterator.next();

				if (fb.getSeverityPattern().map((P) -> P.matcher(next.getSeverity().name()).matches())
						.orElse(Boolean.FALSE)) {
					if (fb.getMessagePattern().map((P) -> P.matcher(next.getMessage()).matches())
							.orElse(Boolean.FALSE)) {
						if (fb.getLocationPattern().map((P) -> P.matcher(next.getLocationString()).matches())
								.orElse(Boolean.FALSE)) {
							iterator.remove();
							if (LOGGER.isLoggable(Level.FINE)) {
								LOGGER.fine(String.format(DEBUG_MESSAGE_FILTERED, next));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Liefert die unveränderliche List mit den Filterbeschreibungen.
	 * 
	 * @return Die Liste. Ist niemals <code>null</code>.
	 */
	public List<FilterBeschreibung> getFilterBeschreibungsListe() {
		return this.filterBeschreibungsListe;
	}

	/**
	 * Schreibt die Datei aus der {@linkplain FilterBeschreibungsListe} in Konsole
	 * aus.
	 * 
	 * @param writer PrintWriter zum Ausgeben ins Konsole.
	 * @throws IllegalArgumentException Wenn übergebene Parameter <code>null</code>
	 *                                  ist.
	 */
	public void marshall(Writer writer) throws IllegalArgumentException {
		if (null == writer) {
			throw new IllegalArgumentException(ERROR_WRITER_NULL);
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FilterBeschreibungsListe.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			FilterBeschreibungsListe filterList = new FilterBeschreibungsListe();
			filterList.setBeschreibungFilterList(filterBeschreibungsListe);

			jaxbMarshaller.marshal(filterList, writer);

		} catch (final Throwable exception) {
			throw new RuntimeException(String.format(ERROR_CANT_WRITE_FILE), exception);
		}
	}

}
