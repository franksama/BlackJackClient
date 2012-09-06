package com.blackjack;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloandroid.R;

public class BlackJack extends Activity {
	private int[] padding = { 20, 20, 20, 20, 20 };

	private EditText betKeypad;
	private int currentPlayer = 1;
	private String player = "";
	private boolean split = false;
	private String bet = "25";

	private DroidClientService service;

	public static final int READ_MSG = 1;
	public static final int WRITE_MSG = 2;
	public static final int TOAST_MSG = 3;
	private static final String TAG = "DroidClient";
	private static final boolean DEBUG = false;

	/* Client Copy */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bet_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener{
    	
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    		//Toast.makeText(parent.getContext(), "The bet is " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
			bet = parent.getItemAtPosition(pos).toString();
		}
    	
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
    }

	public void onStart() {
		super.onStart();
		setupApp();
		service.start();

	}

	public void setupApp() {

		// Service for dealing with the server
		service = new DroidClientService(this, msgHandler);

	}

	private void updatePlayerBudget(int player, String budget) {
		// Set player's bet value

		TextView playerMoney = null;

		if (player == 1) {
			playerMoney = (TextView) findViewById(R.id.playerOneMoney);
		} else if (player == 2) {
			playerMoney = (TextView) findViewById(R.id.playerTwoMoney);
		} else if (player == 3) {
			playerMoney = (TextView) findViewById(R.id.playerThreeMoney);
		} else if (player == 4) {
			playerMoney = (TextView) findViewById(R.id.playerFourMoney);
		}

		if (playerMoney != null) {
			playerMoney.setText("( $ " + budget + " )");
		}
	}
	
	private void updatePlayerCount(int player, String count) {
		TextView playerCount = null;

		if (player == 0) {
			playerCount = (TextView) findViewById(R.id.dealerOutcome);
		} else if (player == 1) {
			playerCount = (TextView) findViewById(R.id.playerOneOutcome);
		} else if (player == 2) {
			playerCount = (TextView) findViewById(R.id.playerTwoOutcome);
		} else if (player == 3) {
			playerCount = (TextView) findViewById(R.id.playerThreeOutcome);
		} else if (player == 4) {
			playerCount = (TextView) findViewById(R.id.playerFourOutcome);
		}

		if (playerCount != null) {
			playerCount.setText("("+count+")");
		}
	}

	private void setPlayerToken(int player, String token) {
		ImageView playerOneCircle = (ImageView) findViewById(R.id.playerOneCircle);
		if (player == 1) {
			if(token.equalsIgnoreCase("1")){
				playerOneCircle.setVisibility(0);
			} else {
				playerOneCircle.setVisibility(View.INVISIBLE);
			}
		}

		ImageView playerTwoCircle = (ImageView) findViewById(R.id.playerTwoCircle);
		if (player == 2) {
			if(token.equalsIgnoreCase("1")){
				playerTwoCircle.setVisibility(0);
			} else {		
				playerTwoCircle.setVisibility(View.INVISIBLE);
			}
		}

		ImageView playerThreeCircle = (ImageView) findViewById(R.id.playerThreeCircle);
		if (player == 3) {
			if(token.equalsIgnoreCase("1")){
				playerThreeCircle.setVisibility(0);
			} else {
				playerThreeCircle.setVisibility(View.INVISIBLE);
			}
		}

		ImageView playerFourCircle = (ImageView) findViewById(R.id.playerFourCircle);
		if (player == 4) {
			if(token.equalsIgnoreCase("1")){
				playerFourCircle.setVisibility(0);
			} else {
				playerFourCircle.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void updatePlayerOutcome(int player, String outcome) {
		TextView playerCount = null;

		if (player == 1) {
			playerCount = (TextView) findViewById(R.id.playerOneOutcome);
		} else if (player == 2) {
			playerCount = (TextView) findViewById(R.id.playerTwoOutcome);
		} else if (player == 3) {
			playerCount = (TextView) findViewById(R.id.playerThreeOutcome);
		} else if (player == 4) {
			playerCount = (TextView) findViewById(R.id.playerFourOutcome);
		}

		if (playerCount != null) {
			
			playerCount.setText("("+outcome+")");
		}
	}

	public void hit(View view) {
		sendMessage("hit");
	}

	public void updateView(String xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader("<root>" + xml + "</root>"));

			Document doc = db.parse(is);
			Element line, line2;
			
			NodeList players = doc.getElementsByTagName("player");
			Cards cardsHelper = new Cards();
			boolean hideBalanceOutcome = false, faceDown = false;
			
			Element[] playerElements = new Element[5];
			
			// set dealer
			Element element = (Element) players.item(0);
			String seat = element.getAttribute("seat");
			playerElements[0] = element;
			
			// iterate through players who have sat
			for (int i = 1; i < players.getLength(); i++) {
				element = (Element) players.item(i);
				
				// get seat
				seat = element.getAttribute("seat");
				if(seat!=null){
					int seatNumber = Integer.parseInt(seat);

					if(seatNumber != -1) {
						seatNumber++;
						playerElements[seatNumber] = element;
					}
				}
			}
			
			// interate through players who have not sat
			for (int i = 1; i < players.getLength(); i++) {
				element = (Element) players.item(i);
				
				// get seat
				seat = element.getAttribute("seat");
				if(seat!=null){
					int seatNumber = Integer.parseInt(seat);

					if(seatNumber == -1) {
						// find empty spot in array
						for (int x = 0; x < playerElements.length; x++) {
							if(playerElements[x] == null) {
								playerElements[x] = element;
								break;
							}
						}
						
					}
				}
			}
			
			for (int i = 0; i < playerElements.length; i++) {
				element = playerElements[i];
				if(element!=null) {
					faceDown = false;
					NodeList name = element.getElementsByTagName("name");
					if(name!=null) {
						line = (Element) name.item(0);
						
						// if the player is the client
						if(player.equalsIgnoreCase(getCharacterDataFromElement(line))) {
							
							TextView playerName = null;
							
							if(i == 1) {
								playerName = (TextView) findViewById(R.id.playerOneName);
							} else if(i == 2) {
								playerName = (TextView) findViewById(R.id.playerTwoName);
							} else if(i == 3) {
								playerName = (TextView) findViewById(R.id.playerThreeName);
							} else if(i == 4) {
								playerName = (TextView) findViewById(R.id.playerFourName);
							}
							
							// set player name
							//playerName.setText(player);
							
							// set player color
							playerName.setTextColor(Color.YELLOW);
							
							/*	<buttons>
									<insurance>0</insurance>
									<sit>0</sit>
									<deal>1</deal>
									<clear>1</clear>
									<bet>1</bet>
									<hit>0</hit>
									<stand>0</stand>
									<split>0</split>
									<double>0</double>
								</buttons>
							 */
							
							// update sit button
							NodeList sit = element.getElementsByTagName("sit");
							if(sit!=null) {
								line = (Element) sit.item(0);
								if(line!=null) {
									Button sitButton = (Button) findViewById(R.id.sit);
									
									if(getCharacterDataFromElement(line).equalsIgnoreCase("1")) {
										sitButton.setVisibility(0);
										
										hideBalanceOutcome = true;
									} else {
										sitButton.setVisibility(View.GONE);
										
										hideBalanceOutcome = false;
									}
								}
							}
							
							// update bet button
							NodeList deal = element.getElementsByTagName("deal");
							if(deal!=null) {
								line = (Element) deal.item(0);
								if(line!=null) {
									Button dealButton = (Button) findViewById(R.id.bet);
									Spinner spinner = (Spinner) findViewById(R.id.spinner);
									if(getCharacterDataFromElement(line).equalsIgnoreCase("1")) {
										dealButton.setVisibility(0);
										
										// show leave button
										Button leaveButton = (Button) findViewById(R.id.leave);
										leaveButton.setVisibility(0);
										
										
										spinner.setVisibility(0);
									} else {
										dealButton.setVisibility(View.GONE);
										spinner.setVisibility(View.GONE);
										// hide leave button
										Button leaveButton = (Button) findViewById(R.id.leave);
										leaveButton.setVisibility(View.GONE);
									}
								}
							}
							
							// update hit button
							NodeList hit = element.getElementsByTagName("hit");
							if(hit!=null) {
								line = (Element) hit.item(0);
								if(line!=null) {
									Button hitButton = (Button) findViewById(R.id.hit);
									
									if(getCharacterDataFromElement(line).equalsIgnoreCase("1")) {
										hitButton.setVisibility(0);
									} else {
										hitButton.setVisibility(View.GONE);
									}
								}
							}
							
							// update stand button
							NodeList stand = element.getElementsByTagName("stand");
							if(stand!=null) {
								line = (Element) stand.item(0);
								if(line!=null) {
									Button standButton = (Button) findViewById(R.id.stand);
									
									if(getCharacterDataFromElement(line).equalsIgnoreCase("1")) {
										standButton.setVisibility(0);
									} else {
										standButton.setVisibility(View.GONE);
									}
								}
							}
							
							
						}
					}
					
					
					// update balance
					NodeList balance = element.getElementsByTagName("balance");
					if(balance!=null) {
						line = (Element) balance.item(0);
						if(hideBalanceOutcome) {
							updatePlayerBudget(i, "--");
						} else {
							updatePlayerBudget(i, getCharacterDataFromElement(line));
						}
					}
					
					// update token
					String token = element.getAttribute("token");
					if(token!=null){
						setPlayerToken(i, token);
					}
					
					
					// update cards
					NodeList hand = element.getElementsByTagName("hand");			
					if(hand!=null) {
						Element hands = (Element) hand.item(0);
						if(hands!=null) {
							NodeList cards = hands.getElementsByTagName("card");	
							
							RelativeLayout relativeView = null;
							if(i == 0) {
								relativeView = (RelativeLayout) findViewById(R.id.dealerLayoutOne);							
							} else if(i == 1) {
								relativeView = (RelativeLayout) findViewById(R.id.playerOneLayoutOne);
							} else if(i == 2) {
								relativeView = (RelativeLayout) findViewById(R.id.playerTwoLayoutOne);
							} else if(i == 3) {
								relativeView = (RelativeLayout) findViewById(R.id.playerThreeLayoutOne);
							} else if(i == 4) {
								relativeView = (RelativeLayout) findViewById(R.id.playerFourLayoutOne);
							}
							
							if(cards.getLength() > 0) {
								// clear previous view and redraw
								while(relativeView.getChildCount() > 0) {
									relativeView.removeView(relativeView.getChildAt(0));
								}
								padding[i] = 20;
							}
							
							for(int x = 0; x < cards.getLength(); x++) {
								Element cardElement = (Element) cards.item(x);
								String upcard = cardElement.getAttribute("upcard");
								String cardValue = getCharacterDataFromElement(cardElement);
								Bitmap card = null;
								
								if(upcard.equalsIgnoreCase("1")){						
									String[] values = cardValue.split(" of ");
									int value = Integer.parseInt(values[0]);
									int suit = cardsHelper.getSuitValue(values[1]);
									card = BitmapFactory.decodeResource(getResources(), cardsHelper.getCard(value, suit));
									
								} else {
									card = BitmapFactory.decodeResource(getResources(), R.drawable.back);
									faceDown = true;
									
								}
								if(card!= null && relativeView != null) {
									ImageView imageView = new ImageView(this);
									imageView.setImageBitmap(card);
									imageView.setPadding(padding[i], 0, 0, 0);
									relativeView.addView(imageView);
									padding[i] += 20;
								}
							}
						}
					}
					// end update cards
					
					String softTotalCount = "", hardTotalCount = "";
					
					// update outcome
					String result = "";
					NodeList action = element.getElementsByTagName("action");
					if(action!=null) {
						Element actionElement = (Element) action.item(0);
						if(actionElement!=null){
							String outcome = actionElement.getAttribute("won");
							if(outcome!=null){
								
								if(hideBalanceOutcome) {
									result = "--";
								} else {
									String outcomeCount = "";
									if(softTotalCount.equalsIgnoreCase(hardTotalCount)) {
										outcomeCount = softTotalCount;
									} else {
										outcomeCount = softTotalCount + "/" + hardTotalCount;
									}
									if(outcome.equalsIgnoreCase("0")) {
										result = "Lose";
									} else if(outcome.equalsIgnoreCase("1")) {
										result = "Won";
									} else if(outcome.equalsIgnoreCase("-1")) {
										result = "";
									}
								}
								//updatePlayerOutcome(i, result);
							}
						}
					}
					
					
					// update card count
					if(!faceDown) {
						NodeList softTotal = element.getElementsByTagName("softtotal");
						NodeList hardTotal = element.getElementsByTagName("hardtotal");
						if(softTotal!=null && hardTotal!=null) {
							line = (Element) softTotal.item(0);
							line2 = (Element) hardTotal.item(0);
							if(line!=null && line2!=null){
								softTotalCount = getCharacterDataFromElement(line);
								hardTotalCount = getCharacterDataFromElement(line2);
								
								NodeList deal = element.getElementsByTagName("deal");
								if(deal!=null) {
									Element dealline = (Element) deal.item(0);
									if(dealline!=null) {
										
										if(getCharacterDataFromElement(dealline).equalsIgnoreCase("1")) {
											//result = "";
										} else {
											//result = "";
										}
									}
								}
								
								if(getCharacterDataFromElement(line).equalsIgnoreCase(getCharacterDataFromElement(line2))) {
									if(!result.equalsIgnoreCase("")) {
										updatePlayerCount(i, result + " " + getCharacterDataFromElement(line));
									} else {
										updatePlayerCount(i, getCharacterDataFromElement(line));
									}
								} else {
									if(!result.equalsIgnoreCase("")) {
										updatePlayerCount(i, result + " " + getCharacterDataFromElement(line) + "/" + getCharacterDataFromElement(line2));
									} else {
										updatePlayerCount(i, getCharacterDataFromElement(line) + "/" + getCharacterDataFromElement(line2));
									}
								}
							}
						}
					}
				
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

	public void split(View view) {
		split = true;
		if (currentPlayer == 1) {
			/*
			 * Bitmap card = BitmapFactory.decodeResource(getResources(),
			 * R.drawable.four_spades); RelativeLayout relativeView =
			 * (RelativeLayout)findViewById(R.id.playerOneLayoutTwo); ImageView
			 * imageView = new ImageView(this); imageView.setImageBitmap(card);
			 * imageView.setPadding(padding[1], 0, 0, 0);
			 * relativeView.addView(imageView); padding[1]+=20;
			 */
		}

		if (currentPlayer == 2) {

		}

		if (currentPlayer == 3) {

		}

		if (currentPlayer == 4) {

		}
	}

	public void bet(View view) {		
		
		
		RelativeLayout relativeView = null;
		
		TextView dealerOutcome = (TextView) findViewById(R.id.dealerOutcome);
		dealerOutcome.setText("");
		
		for(int i = 0; i < 5; i++) {
			if(i == 0) {
				relativeView = (RelativeLayout) findViewById(R.id.dealerLayoutOne);							
			} else if(i == 1) {
				relativeView = (RelativeLayout) findViewById(R.id.playerOneLayoutOne);
			} else if(i == 2) {
				relativeView = (RelativeLayout) findViewById(R.id.playerTwoLayoutOne);
			} else if(i == 3) {
				relativeView = (RelativeLayout) findViewById(R.id.playerThreeLayoutOne);
			} else if(i == 4) {
				relativeView = (RelativeLayout) findViewById(R.id.playerFourLayoutOne);
			}
		
		
			// clear previous view and redraw
			while(relativeView.getChildCount() > 0) {
				relativeView.removeView(relativeView.getChildAt(0));
			}
			
			
			padding[i] = 20;
		}
		
		sendMessage("deal:"+bet.substring(1));
	}

	public void join(View view) {
		
		// hide join button once player joins
		Button joinButton = (Button) findViewById(R.id.join);
		joinButton.setVisibility(View.GONE);
		
		// generate random player number
		Random generator = new Random();
		player = Integer.toString(generator.nextInt(10000000)); 
		
		sendMessage("user:"+player);
	}

	public void sit(View view) {
		sendMessage("sit");
	}
	
	public void leave(View view) {
		sendMessage("exit");
	}
	
	public void stand(View view) {
		sendMessage("stand");
	}

	private void sendMessage(String msg) {
		if (msg.length() > 0) {
			// Send message here
			service.write(msg);
		} else {
			return;
		}
	}

	public void onResume() {
		super.onResume();
		if (DEBUG) {
			Log.e(TAG, "--OnResume--");
		}
		service.start();
	}

	public void onRestart() {
		super.onRestart();
	}

	public void onStop() {
		super.onStop();
		if (service != null)
			service.stop();
		// Kill anything else here
	}

	public void onDestroy() {
		super.onDestroy();
		if (service != null)
			service.stop();
		// Kill anything else here
	}

	public void onPause() {
		super.onPause();
	}

	private final Handler msgHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WRITE_MSG:
				String write = (String) msg.obj;
				String out = new String(write);
				break;
			case READ_MSG:
				String in = (String) msg.obj;
				updateView(in);
				break;
			case TOAST_MSG:
				Toast.makeText(getApplicationContext(), msg.getData().getString("TOAST"), Toast.LENGTH_SHORT);
				break;
			}
		}
	};
}