package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;

import app.implementations.MessengerImpl;
import app.interfaces.Messenger;
import app.utils.HostsFileParser;
import app.utils.Message;

public class Application {

	private static final int TIMEOUT = 100;
	private static final int PORT = 1234;

	public static void main(String[] args)
			throws FileNotFoundException, IOException, ParseException, ClassNotFoundException {

		HashSet<String> proposers = HostsFileParser.parse("src/main/resources/hosts.json", "proposers");
		HashSet<String> storages = HostsFileParser.parse("src/main/resources/hosts.json", "storages");

		Messenger messenger = new MessengerImpl(PORT, TIMEOUT);

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in).useDelimiter("\n");
		Pattern write = Pattern.compile("write (\\p{Alnum}+)=(\\p{Alnum}+)");
		Pattern read = Pattern.compile("read (\\p{Alnum}+)");
		Pattern quit = Pattern.compile("quit");
		Matcher matcher;
		String input;
		Message message;
		Message reply;

		while (true) {
			input = scanner.next();

			// parsing write command
			matcher = write.matcher(input);
			if (matcher.matches()) {
				message = new Message();
				message.setType("write");
				message.setAcceptedValue(
						new AbstractMap.SimpleEntry<String, String>(matcher.group(1), matcher.group(2)));
				System.out.println("Zapisywanie wartości " + matcher.group(2) + " pod kluczem " + matcher.group(1));
				for (String proposerIP : proposers)
					try {
						messenger.send(proposerIP, message);
					} catch (IOException e) {
						System.err.println(proposerIP + " jest nieaktywny");
					}
				continue;
			}

			// parsing read command
			matcher = read.matcher(input);
			if (matcher.matches()) {
				message = new Message();
				message.setType("read");
				message.setAcceptedValue(matcher.group(1));
				System.out.println("Odczywt wartości spod klucza " + matcher.group(1));
				reply = null;
				for (String storageIP : storages) {
					try {
						reply = messenger.sendReceive(storageIP, message);
						System.out.println(matcher.group(1) + " = " + reply.getAcceptedValue());
						break;
					} catch (IOException e) {
						System.err.println("Storage " + storageIP + " jest nieaktywny");
					}
					if (reply != null)
						break;
				}
				continue;
			}

			// parsing quit command
			matcher = quit.matcher(input);
			if (matcher.matches()) {
				System.out.println("Zamykanie programu");
				break;
			}
		}

		scanner.close();

	}

}
