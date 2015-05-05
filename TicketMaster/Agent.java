/**
 * This program automates checking ticket information and booking ticket online.
 * 
 * The steps are:
 * - check ticket information
 * - book tour
 * - login
 * - checkout
 * - process
 * - logout
 * 
 * @by X.C.
 * @since 4/19/2015
 * @last modified: 4/20/2015
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * The Class Agent.
 */
public class Agent {

	/** The cookies. */
	private List<String> cookies;

	// HttpURLConnection is base class of HttpsURLConnection.
	// So with conn declared as HttpURLConnection, it can be
	// instantiated to both types.
	/** The web connection. */
	private HttpURLConnection conn;

	/** The Configuration file. */
	private final String Config_File = "Agent.config";
	
	/** The browser's user agent. */
	private final String USER_AGENT = "Mozilla/5.0";

	// ///////////////////////////////////////////////////////////////////
	// Urls.
	// ///////////////////////////////////////////////////////////////////

	/** The login url. */
	private static String login_url = "https://www.recreation.gov/memberSignInSignUp.do";
	
	/** The logout url. */
	private static String logout_url = "http://www.recreation.gov/memberSignOut.do";
	
	/** The ticket url. */
	private static String ticket_url = "http://www.recreation.gov/tourDetails.do?contractCode=NRSO&tourId=$tourId";
	
	/** The book-tour url. */
	private static String bookTour_url = "http://www.recreation.gov/bookTour.do?contractCode=NRSO&tourId=$tourId&tourDate=$1&cat=0&numberOfTicketsSearched=$2";
	
	/** The checkout url. */
	private static String checkout_url = "https://www.recreation.gov/checkoutShoppingCart.do";
	
	/** The process url. */
	private static String process_url = "https://www.recreation.gov/processCart.do";

	/** The Constant host. */
	private final static String host = "www.recreation.gov";
	
	/** The url referrer. */
	private String referer = ticket_url;

	// ///////////////////////////////////////////////////////////////////
	// Form constants.
	// ///////////////////////////////////////////////////////////////////

	/** Login form. */
	static String form_login = "existing_cust";

	/** The input username. */
	static String input_username = "AemailGroup_1733152645";
	
	/** The input password. */
	static String input_password = "ApasswrdGroup_704558654";
	
	// Login works after adding in this hidden element in postParams.
	// Note this hidden input element is not in return page html,
	// guess it's dynamically written by javascript.
	// This Java program simulates browser, but it does not support javascript.
	// This hidden element is:
	// <input value="combinedFlowSignInKit" id="combinedFlowSignInKit__sbmtCtrl"
	// name="sbmtCtrl" type="hidden">
	/** The form_login_hidden_param. */
	static String form_login_hidden_param = "&sbmtCtrl=combinedFlowSignInKit";

	
	/** The tourAvail form. */
	static String form_tourAvail = "tourAvailForm";

	/** The input tour date. */
	static String input_tourDate = "tourDate";
	
	/** The input num tickets searched. */
	static String input_numTicketsSearched = "numberOfTicketsSearched";
	
	/** The div tourStatus message. */
	static String div_tourStatusMessage = "tourStatusMessage";


	/** The bookTour form. */
	static String form_bookTour = "bookTourForm";

	/** The select time_ id. */
	static String selectTime_ID = "T317399timeSelect";
	
	/** The select dropdown list for time. */
	static String selectTime_Name = "invId";
	
	/** The input tickTypeQty. */
	static String input_tickTypeQty = "tickTypeQty";


	/** The checkout form. */
	static String form_checkout = "checkoutCartForm";

	/** The input cardNumber. */
	static String input_cardNumber = "cardNumber";
	
	/** The input securityCode. */
	static String input_securityCode = "securityCode";
	
	/** The input expMonth. */
	static String input_expMonth = "expMonth";
	
	/** The input expYear. */
	static String input_expYear = "expYear";
	
	/** The input firstName. */
	static String input_firstName = "firstName";
	
	/** The input lastName. */
	static String input_lastName = "lastName";
	
	/** The input ackAccepted. */
	static String input_ackAccepted = "acknowlegeAccepted";
	
	/** The input cardType. */
	static String input_cardType = "cardType";

	// ///////////////////////////////////////////////////////////////////
	// keywords.
	//
	// These keywords are used to check if a page is correctly
	// downloaded, i.e., contains these keywords.
	// ///////////////////////////////////////////////////////////////////

	/** The keyword in login page - for personal profile page. */
	static String keyword_login = "Profile Overview";

	/** The keyword in profile page - for profile line in profile page. */
	static String keyword_profile = "Name:";

	/** The keyword checkout - for checkout page. */
	static String keyword_checkout = "Shopping Cart";

	// ///////////////////////////////////////////////////////////////////
	// User configurations.
	// These are the informatin a user should provide.
	// ///////////////////////////////////////////////////////////////////

	/** The user. */
	static String _user;
	
	/** The pass. */
	static String _pass;
	
	/** The ticket number. */
	static String _ticketNum;
	
	/** The ticket date. */
	static String _ticketDate;

	/** The card number. */
	static String _cardNumber;
	
	/** The security code. */
	static String _securityCode;
	
	/** The expiration month. */
	static String _expMonth;
	
	/** The expiration year. */
	static String _expYear;
	
	/** The first name. */
	static String _firstName;
	
	/** The last name. */
	static String _lastName;

	/** 
	 * The card type. 
	 * Card types can be:
	 * VISA, MAST (Master), DISC (Discover/JCB/UnionPay), AMEX (American Express)
	 * */
	static String _cardType;
	
	/** The Constant ackAccepted. */
	static final String _ackAccepted = "true"; // This never changes.
	
	/** The tour id. */
	static String _tourId;

	/** The redirect_url. */
	static String _redirect_url;
	
	/** The tickets info. */
	static String ticketsInfo;

	/** The debug flag. */
	private static int DEBUG = 0;
	
	/** The operation option value. */
	private static int opt;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		if (!getArgs(args))
			return;

		// Make sure cookie is turned on.
		CookieHandler.setDefault(new CookieManager());

		Agent http = new Agent();
		http.handle(opt);
	}

	/**
	 * Gets the arguments.
	 *
	 * @param args the arguments
	 * @return Boolean - whether the arguments are read correctly.
	 */
	public static Boolean getArgs(String[] args) {
		try {
			getOptions(args);
		} catch (Exception e) {
			output("option error: " + e.getMessage());
			showUsage();
			return false;
		}

		if (opt == -1) {
			showUsage();
			return false;
		}

		return true;
	}

	/**
	 * Gets the options.
	 *
	 * @param args the arguments
	 * @return void
	 * @throws Exception the exception
	 */
	public static void getOptions(String[] args) throws Exception {
		int len = args.length;
		if (len == 0) {
			throw new Exception("no operation is provided");
		}

		opt = DEBUG = -1;

		// Two modes: is_opt_name / is_opt_value
		Boolean is_key = true;
		String key = "", arg = "";

		for (int i = 0; i < len; ++i) {
			arg = args[i];

			if (is_key) {
				if (!arg.equals("-o") && !arg.equals("-d")) {
					throw new Exception("unknown option switch: " + key);
				} else {
					key = arg;
					is_key = false;
				}
			} else { // is value
				if (key.equals("-o")) {
					opt = getOptionVal(arg, key, opt);
				} else if (key.equals("-d")) {
					DEBUG = getOptionVal(arg, key, DEBUG);
				}
				is_key = true;
			}
		}

		// Make sure parameters are in correct range.
		if (opt < 0 || opt > 3)
			opt = -1;
		if (DEBUG < 0 || DEBUG > 7)
			DEBUG = 0;
	}

	/**
	 * Gets the option value.
	 *
	 * Option value should be non-negative integer. If < 0, then it's an error.
	 *
	 * @param s the input string for option value.
	 * @param key the key
	 * @param val the value
	 * @return the option value in integer type
	 * @throws Exception the exception
	 */
	private static int getOptionVal(String s, String key, int val)
			throws Exception {
		if (val != -1) {
			throw new Exception("switch " + key + " already assigned");
		}

		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			throw new Exception("switch " + key + " value is not integer: " + s);
		}
	}


	/**
	 * Debug cookie.
	 *  
	 * Whether to show cookie information.
	 *
	 * @return the boolean
	 */
	private static Boolean debugCookie() {
		return (DEBUG & 1) != 0;
	}

	/**
	 * Debug Get/Post.
	 * 
	 * Whether to show get/post details.
	 *
	 * @return the boolean
	 */
	private static Boolean debugGetPost() {
		return (DEBUG & 2) != 0;
	}

	/**
	 * Debug html.
	 * 
	 * Whether to display page html.
	 *
	 * @return the boolean
	 */
	private static Boolean debugHtml() {
		return (DEBUG & 4) != 0;
	}

	/**
	 * Debug html src.
	 *
	 * If this option is on, in html all "<" tags are translated to "<br/>&lt;",
	 * so is easy to view in browser.
	 * 
	 * @return the boolean
	 */
	private static Boolean debugHtmlSrc() {
		return (DEBUG & 8) != 0;
	}

	/**
	 * Show usage.
	 */
	public static void showUsage() {
		output("usage: agent -o option [-d debug_mode]");
		output("option: 0 - show config, 1 - test login, 2 - get ticket info, 3 - book ticket");
		output("debug_mode: 0 - none, 1 - cookies, 2 - get/post, 4 - html, 8 - show source");
	}

	/**
	 * Gets the configurations.
	 * 
	 * In configuration file,
	 * every line (after trim) that is empty or starts with "#" are ignored.
	 * Every configuration line has the format: key = value
	 * Spaces around "=" does not matter.
	 * 
	 * @return Boolean - whether the configuration is read correctly.
	 * @throws Exception the exception
	 */
	public Boolean getConfig() throws Exception {
		output("\n++++++++ Get User Configuration +++++++++++");

		try {
			HashMap<String, String> hm = readConfigFile();

			_user = getConfigParam(hm, "User");
			_pass = getConfigParam(hm, "Pass");

			_ticketNum = getConfigParam(hm, "TicketNum");
			_ticketDate = getConfigParam(hm, "TicketDate");

			_cardNumber = getConfigParam(hm, "CardNumber");
			_securityCode = getConfigParam(hm, "SecurityCode");
			_expMonth = getConfigParam(hm, "ExpMonth");
			_expYear = getConfigParam(hm, "ExpYear");
			_firstName = getConfigParam(hm, "Firstname");
			_lastName = getConfigParam(hm, "Lastname");
			_cardType = getConfigParam(hm, "CardType");

			_tourId = getConfigParam(hm, "TourId");
			ticket_url = ticket_url.replace("$tourId", _tourId);
			bookTour_url = bookTour_url.replace("$tourId", _tourId);

		} catch (Exception e) {
			output("getConfig() error: " + e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Gets a configuration parameter.
	 *
	 * All the fields should exist and is not empty.
	 * Exception for ticketDate: if empty, return default, which is tomorrow.
	 * Note: HashMap allows null key and/or value, HashTable does not.
	 *
	 * @param hm A hash map
	 * @param cap_key The key, some letters may be capitalized
	 * @return the configuration parameter as a string
	 * @throws Exception the exception
	 */
	private String getConfigParam(HashMap<String, String> hm, String cap_key)
			throws Exception {
		String val = "";
		String key = cap_key.toLowerCase();

		if (key.equals("ticketdate")) {
			if (!hm.containsKey(key) || hm.get(key) == null
					|| hm.get(key).equals("")) {
				val = getDefaultTicketDate();
				// output("...ticketdate date default val: " + val);
			} else {
				val = hm.get(key);
			}
		} else {
			if (!hm.containsKey(key)) {
				throw new Exception("Value is needed for: " + key);
			}
			val = hm.get(key);
			if (val == "") {
				throw new Exception("Value cannot be empty: " + key);
			}
		}

		if (key.equals("pass"))
			output(cap_key + ": ********");
		else if (key.equals("cardnumber"))
			output(cap_key + ": ************" + val.substring(val.length() - 4));
		else if (key.equals("securitycode"))
			output(cap_key + ": " + "***");
		else
			output(cap_key + ": " + val);

		return val;
	}

	/**
	 * Gets the default ticket date.
	 *
	 * @return the default ticket date
	 */
	private String getDefaultTicketDate() {
		Date todaysDate = new Date();
		Date tomorrowsDate = addDays(todaysDate, 1);
		DateFormat dateFormat = new SimpleDateFormat("E MMM dd yyyy");
		String date = dateFormat.format(tomorrowsDate);
		return date;
	}

	/**
	 * Adds days to give date.
	 *
	 * @param date the date
	 * @param days the number of days to add
	 * @return the resulted date
	 */
	public Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	/**
	 * Read configuration file.
	 * 
	 * If there is any error, throw the exception, let getConfig() handle it.
	 *
	 * @return the hash map
	 * @throws Exception the exception
	 */
	private HashMap<String, String> readConfigFile() throws Exception {
		HashMap<String, String> hm = new HashMap<String, String>();

		BufferedReader br = new BufferedReader(new FileReader(Config_File));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				line = line.trim();
				// sb.append(line);
				// sb.append(System.lineSeparator());
				if (!(line.equals("") || line.startsWith("#"))) {
					// output(":" + line);
					String[] items = line.split("=");
					if (items.length == 2) {
						String key = items[0].trim().toLowerCase();
						String val = items[1].trim();
						hm.put(key, val);
					} else {
						// output("incorrect config (should be 'key = value'): "
						// + line);
					}
				}
				line = br.readLine();
			}
			String everything = sb.toString();
		} catch (Exception e) {
			throw new Exception("readConfigFile() error: " + e.toString());
		} finally {
			br.close();
		}

		return hm;
	}

	/**
	 * Handles task depends on opt value.
	 *
	 * @param opt the option value
	 * @throws Exception the exception
	 */
	public void handle(int opt) throws Exception {
		showOpt(opt);
		if (!getConfig())
			return;

		if (opt == 1) {
			doLogin(1);
			doLogout();
		} else if (opt == 2) {
			doCheckTicket(false);
		} else if (opt == 3) {
			doCheckTicket(true);
			// should logout here too.
		} else if (opt == 0) {
			// test showOpt() and getConfig().
			// do nothing here.
		} else {
			// not implemented.
		}
	}

	/**
	 * Show option value.
	 *
	 * @param opt the opt
	 */
	private void showOpt(int opt) {
		String name = "Unknown option";
		if (opt == 0)
			name = "Show config";
		else if (opt == 1)
			name = "Test login";
		else if (opt == 2)
			name = "Check ticket information";
		else if (opt == 3)
			name = "Book tour";

		output("\noption = " + opt + ": " + name);
	}

	/**
	 * Do logout.
	 *
	 * @throws Exception the exception
	 */
	public void doLogout() throws Exception {
		output("\n++++++++ Logout +++++++++++++++++++++++++++");
		String page = GetPageContent(logout_url);
		output("=== Logout Done ===");
	}

	/**
	 * Do login.
	 *
	 * @param source the source: 1 - direct login, 2 - doCheckTicket.
	 * @throws Exception the exception
	 */
	public void doLogin(int source) throws Exception {
		if (!certifyLoginSource(source))
			return;

		String numTickets = "1";
		String page, postParams;

		// First get login page content.
		output("\n++++++++ get login page content +++++++++++");
		page = this.GetPageContent(login_url);

		postParams = getFormParams(page, form_login);

		if (postParams == "") {
			output("doLogin(): no parameter found in form: " + form_login);
			return;
		}

		// Login works after adding in this hidden element in postParams.
		// note this hidden input element is not in return page html.
		// guess it's dynamically written by javascript.
		// Java can simulate browser request, but does not support javascript
		// anyways.
		// This hidden element is:
		// <input value="combinedFlowSignInKit"
		// id="combinedFlowSignInKit__sbmtCtrl" name="sbmtCtrl" type="hidden">
		postParams += form_login_hidden_param;

		// Login
		output("\n++++++++ Login ++++++++++++++++++++++++++++");
		page = sendPost(login_url, postParams);

		Boolean loginOK;
		String keyword = getKeyword(source);

		loginOK = (page.indexOf(keyword) > 0);

		if (!loginOK) {
			output("*** Login failed ***");
			return; // Stop if login fails.
		}

		output("*** Login succeeded ***");

		if (source == 1) {
			output(GetProfile(page));
			output("=== Login Done ===");
		} else if (source == 2) {
			output(GetBookInfo(page));
			doCheckout();
		}
		// else, already checked in certifySource().
	}

	/**
	 * Certify login source: where login() function is called from.
	 *
	 * @param source the source
	 * @return the boolean
	 */
	private Boolean certifyLoginSource(int source) {
		if (source == 1 || source == 2) {
			return true;
		} else {
			output("doLogin().certifyLoginSource(): unknown source (should be 1 or 2): "
					+ source);
			return false;
		}
	}

	/**
	 * Gets the keyword. To judge if html download is successful.
	 *
	 * @param source the source
	 * @return the keyword
	 * @throws Exception the exception
	 */
	private String getKeyword(int source) throws Exception {
		String keyword = "";
		if (source == 1) {
			keyword = keyword_login;
		} else if (source == 2) {
			keyword = keyword_checkout;
		}
		// else, already checked in certifySource().
		return keyword;
	}

	/**
	 * Do checkout.
	 *
	 * @throws Exception the exception
	 */
	public void doCheckout() throws Exception {
		output("\n++++++++ Checkout +++++++++++++++++++++++++");
		String page, postParams;

		page = this.GetPageContent(checkout_url);
		postParams = this.getFormParams(page, form_checkout);

		if (debugGetPost()) {
			output("doCheckout() params: " + postParams);
		}
		/*
		 * page = http.sendPost(process_url, postParams); output(page);
		 */
		output("=== Book Ticket Done ===");
	}

	/**
	 * Do check ticket information.
	 *
	 * @param checkout Boolean, if true, proceed to checkout after checking ticket information.
	 * @throws Exception the exception
	 */
	public void doCheckTicket(Boolean checkout) throws Exception {
		output("\n++++++++ Check Ticket Information +++++++++");
		String page, postParams;

		page = this.GetPageContent(ticket_url);
		postParams = this.getFormParams(page, "tourAvailForm");

		page = this.sendPost(ticket_url, postParams);
		postParams = this.getFormParams(page, "bookTourForm");

		// Now should scan ticketsInfo to find a good section.
		this.scanTicketInfo(ticketsInfo);

		if (!checkout) { // If don't checkout. stop here now.
			output("=== Check Ticket Information Done ===");
			return;
		}

		if (ticketsInfo.indexOf("-- Sold Out --") >= 0) {
			output("Cannot book tour because ticket is sold out. End.");
			return;
		} else if (ticketsInfo.equals("")) {
			output("Cannot book tour because ticket is not available. End.");
			return;
		}

		// now post for booking
		// At this step, needs login cookie, otherwise redirected.
		// output("raw bookTour_url: " + bookTour_url + "\n");
		bookTour_url = bookTour_url.replace("$1",
				URLEncoder.encode(_ticketDate, "UTF-8"));
		bookTour_url = bookTour_url.replace("$2", _ticketNum);
		// output("processed bookTour_url: " + bookTour_url);
		page = this.sendPost(bookTour_url, postParams);

		if (page == "302") {
			if (debugGetPost()) {
				output("page = 302. redirect to: " + _redirect_url);
			}
			login_url = _redirect_url;
			doLogin(2);
		} else {
			output("CheckTicket: Should redirect to login if ticket is not sold out.");
			output("If ticket is not sold out, check with developer.");
		}
	}

	/**
	 * Scan ticket information.
	 *
	 * @param ticketsInfo the tickets info
	 */
	private void scanTicketInfo(String ticketsInfo) {
		if (ticketsInfo.equals("")) {
			output("\n===Ticket Info===\nNo ticket is available\n");
		} else {
			output("\n===Ticket Info===\n" + ticketsInfo);
		}
	}

	/**
	 * Output. 
	 * 
	 * This centralizes all output. Now to stdout. Later can to log files etc.
	 *
	 * @param s the s
	 */
	private static void output(String s) {
		System.out.println(s);
	}

	/**
	 * Send post.
	 *
	 * @param url the url
	 * @param postParams the post parameters
	 * @return the post response string
	 * @throws Exception the exception
	 */
	private String sendPost(String url, String postParams) throws Exception {
		URL obj = new URL(url);

		if (url.startsWith("https:")) {
			conn = (HttpsURLConnection) obj.openConnection();
		} else {
			conn = (HttpURLConnection) obj.openConnection();
		}

		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		// conn.setRequestProperty("Host", "accounts.google.com");
		conn.setRequestProperty("Host", host); // /
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : this.cookies) {
			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		conn.setRequestProperty("Connection", "keep-alive");
		// conn.setRequestProperty("Referer",
		// "https://accounts.google.com/ServiceLoginAuth");
		conn.setRequestProperty("Referer", referer); // /
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",
				Integer.toString(postParams.length()));

		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();

		if (debugGetPost()) {
			output("\nSending 'POST' request to URL : " + url);
			output("Post parameters : " + postParams);
			output("Response Code : " + responseCode);
		}

		if (responseCode == 302) {
			// get redirect url from "location" header field
			_redirect_url = conn.getHeaderField("Location");

			// get the cookie if need, for login. Nothing new in this.
			// String cookies = conn.getHeaderField("Set-Cookie");

			// output("302 redirection url: " + _redirect_url); // +
			// "\ncookie: " + cookies);
			return "302";
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		// dumpCookies(conn.getHeaderFields().get("Set-Cookie")); /// same as
		// last get request.

		// if (DEBUG) System.out.println(response.toString());
		String page = response.toString();

		if (debugHtml())
			output(page);
		else if (debugHtmlSrc())
			output(page.replace("<", "<br/>&lt;"));

		return page;
	}

	/**
	 * Gets the page content of given url.
	 *
	 * @param url the url
	 * @return the page content as a string
	 * @throws Exception the exception
	 */
	private String GetPageContent(String url) throws Exception {
		// output("GetPageContent(url). url = " + url);

		URL obj = new URL(url);

		if (url.startsWith("https:")) {
			conn = (HttpsURLConnection) obj.openConnection();
		} else {
			conn = (HttpURLConnection) obj.openConnection();
		}

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();

		if (debugGetPost()) {
			output("\nSending 'GET' request to URL : " + url);
			output("Response Code : " + responseCode);
		}

		if (responseCode == 302) {
			// get redirect url from "location" header field
			_redirect_url = conn.getHeaderField("Location");

			// get the cookie if need, for login. Nothing new in this.
			// String cookies = conn.getHeaderField("Set-Cookie");

			// output("302 redirection url: " + _redirect_url);
			return "302";
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));

		String page = response.toString();

		if (debugHtml())
			output(page);
		else if (debugHtmlSrc())
			output(page.replace("<", "<br/>&lt;"));

		return page;
	}

	/**
	 * Gets the profile of a user from given html.
	 *
	 * @param html the html
	 * @return the string of a user's profile
	 * @throws Exception the exception
	 */
	private String GetProfile(String html) throws Exception {
		String profile = "\n== GetProfile ==\n";

		Document doc = Jsoup.parse(html);
		Elements es = doc.getElementsByClass("inbx");
		if (es == null) {
			return "profile not found\n";
		}

		for (Element e : es) {
			String txt = e.text();
			if (txt.startsWith(keyword_profile)) {
				profile += txt + "\n";
			}
		}
		return profile;
	}

	/**
	 * Gets the booking information.
	 *
	 * @param html the html
	 * @return the string for booking information
	 * @throws Exception the exception
	 */
	private String GetBookInfo(String html) throws Exception {
		String profile = "\n== GetBookInfo ==\n";

		Document doc = Jsoup.parse(html);
		Element e = doc.getElementById("table1");
		if (e == null) {
			return "book information not found\n";
		}

		String info = removeNonAsciiChar(e.text());

		profile += info + "\n";
		return profile;
	}

	/**
	 * Removes the non-ascii characters.
	 * 
	 * Remove chars not in '\r', '\n' and 0x20 - 0x7E from input string.
	 *
	 * @param s the s
	 * @return the string
	 */
	private String removeNonAsciiChar(String s) {
		return s.replaceAll("[^\\x0A\\x0D\\x20-\\x7E]", "\n");
	}

	/*
	 * private List<String> getSelectParams(Document doc, String selectID) {
	 * Element mySelect = doc.getElementById(selectID); if (mySelect != null) {
	 * Elements options = mySelect.getElementsByTag("option"); for (Element
	 * option : options) { //if
	 * (option.attr("value").equals(optionValueToBeSelected)) { //
	 * option.attr("selected", "selected"); //} else { //
	 * option.removeAttr("selected"); //}
	 * 
	 * //if (DEBUG) output("option: " + option.attr("value") + " " +
	 * option.text()); param_select = "invId=" +
	 * URLEncoder.encode(option.attr("value"), "UTF-8"); ticketsInfo +=
	 * option.attr("value") + " " + option.text() + "\n"; } } }
	 */

	/**
	 * Gets the form input parameters, fill given values (from configuration file).
	 *
	 * @param doc the doc
	 * @param formID the form id
	 * @param hm a hash map
	 * @return the form input parameters as a string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private String getFormInputParams(Document doc, String formID, HashMap hm)
			throws UnsupportedEncodingException {
		Element form = doc.getElementById(formID);
		if (form == null) {
			output("\nError: form not found: " + formID);
			return null;
		}

		Elements inputElements = form.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (hm.containsKey(key))
				value = hm.get(key).toString();

			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}

		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}

		return result.toString();
	}

	/**
	 * Gets login form parameters.
	 *
	 * @param doc the doc
	 * @param formID the form id
	 * @return the form params_login form
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private String getFormParams_loginForm(Document doc, String formID)
			throws UnsupportedEncodingException {
		HashMap<String, String> hm = new HashMap<String, String>();

		hm.put(input_username, _user);
		hm.put(input_password, _pass);

		return getFormInputParams(doc, formID, hm);
	}

	/**
	 * Gets the form parameters in tourAvail form.
	 *
	 * @param doc the doc
	 * @param formID the form id
	 * @return the form parameters of tourAvail form
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private String getFormParams_tourAvailForm(Document doc, String formID)
			throws UnsupportedEncodingException {
		HashMap<String, String> hm = new HashMap<String, String>();

		hm.put(input_tourDate, _ticketDate);
		hm.put(input_numTicketsSearched, "1");

		return getFormInputParams(doc, formID, hm);
	}

	/**
	 * Gets the form parameters in bookTour form.
	 *
	 * @param doc the doc
	 * @param formID the form id
	 * @return the form params_book tour form
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private String getFormParams_bookTourForm(Document doc, String formID)
			throws UnsupportedEncodingException {
		HashMap<String, String> hm = new HashMap<String, String>();

		hm.put(input_tickTypeQty, "1");

		String params = getFormInputParams(doc, formID, hm);
		ticketsInfo = "";

		if (params == null) {
			Element status = doc.getElementById(div_tourStatusMessage);
			if (status != null && !status.text().equals("")) {
				output(status.text());
				return "";
			}
		}

		String param_select = "";

		Element mySelect = doc.getElementById(selectTime_ID);
		if (mySelect != null) {
			Elements options = mySelect.getElementsByTag("option");
			for (Element option : options) {
				// if (DEBUG) output("option: " + option.attr("value") +
				// " " + option.text());
				ticketsInfo += option.attr("value") + " | " + option.text()
						+ "\n";
				param_select = selectTime_Name + "="
						+ URLEncoder.encode(option.attr("value"), "UTF-8");
			}
		}

		// Note here param_select is always the last item.
		// This does not really mater since input_tickTypeQty is "1".
		if (!param_select.equals(""))
			params += "&" + param_select;

		return params;
	}

	/**
	 * Gets the form parameters in checkout form.
	 *
	 * @param doc the doc
	 * @param formID the form id
	 * @return the form params_checkout form
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private String getFormParams_checkoutForm(Document doc, String formID)
			throws UnsupportedEncodingException {
		HashMap<String, String> hm = new HashMap<String, String>();

		hm.put(input_cardNumber, _cardNumber);
		hm.put(input_securityCode, _securityCode);
		hm.put(input_expMonth, _expMonth);
		hm.put(input_expYear, _expYear);
		hm.put(input_firstName, _firstName);
		hm.put(input_lastName, _lastName);
		hm.put(input_ackAccepted, _ackAccepted);

		String params = getFormInputParams(doc, formID, hm);
		params += "&" + input_cardType + "=" + _cardType;

		return params;
	}

	/**
	 * Gets form parameters from html depending on formID.
	 *
	 * @param html the html
	 * @param formID the form id
	 * @return the form parameters as a string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public String getFormParams(String html, String formID)
			throws UnsupportedEncodingException {
		// output("Extracting form's data...");

		Document doc = Jsoup.parse(html);
		String params = "";

		if (formID == this.form_login) {
			params = getFormParams_loginForm(doc, formID);
		} else if (formID == this.form_tourAvail) {
			params = getFormParams_tourAvailForm(doc, formID);
		} else if (formID == this.form_bookTour) {
			params = getFormParams_bookTourForm(doc, formID);
		} else if (formID == this.form_checkout) {
			params = getFormParams_checkoutForm(doc, formID);
		} else {
			output("getFormParams(): Error: unknown form: " + formID);
		}

		return params;
	}

	/**
	 * Gets the cookies.
	 *
	 * @return the cookies
	 */
	public List<String> getCookies() {
		return cookies;
	}

	/**
	 * Sets the cookies.
	 *
	 * @param cookies the new cookies
	 */
	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
		this.dumpCookies(cookies);
	}

	/**
	 * Dump cookies.
	 *
	 * @param cookies the cookies
	 */
	public void dumpCookies(List<String> cookies) {
		if (debugCookie()) {
			output("===cookies===");
			if (cookies == null) {
				output("cookie is null.");
				return;
			}
			for (String s : cookies) {
				output("cookie: " + s);
			}
		}
	}
}
