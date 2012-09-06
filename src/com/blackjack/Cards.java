package com.blackjack;
import android.graphics.Bitmap;

import com.example.helloandroid.R;


/* This class contains methods that will not be used in blackjack but can be used in other card games.
 * class Cards gives have methods in determining what type of card it is by supplying the user with the
 * face value, the name of the value, and what type of suit it is.
*/
class Cards
{
	private int value;
	private String name;
	private int suit;
	private String suitname;
	
	// default constructor
	public Cards ()
	{
		value = 0;
		name = "Spade";
		
	}


	// pre: value must be between 1 and 13
	// post: value gets newValue
	public Cards (int newValue)
	{
		value = newValue;
	}
	

	// pre: value must be between 1 and 13 
	//      suit must be between 0 and 3
	// post: assigns value to newValue; assigns suit to newSuit; assignName method
	//		 is called to assign appropriate name.
	public Cards (int newValue, int newSuit)
	{
		value = newValue;
		suit = newSuit;
		assignName (value);
	}
	
	// pre: value must be between 1 and 13
	// post: name is properly assigned the correct value
	private void assignName (int value)
	{
		switch (value)
		{
			case 1:
				name = "Ace";
				break;
			case 2:
				name = "Two";
				break;
			case 3:
				name = "Three";
				break;
			case 4:
				name = "Four";
				break;
			case 5:
				name = "Five";
				break;
			case 6:
				name = "Six";
				break;
			case 7:
				name = "Seven";
				break;
			case 8:
				name = "Eight";
				break;
			case 9:
				name = "Nine";
				break;
			case 10:
				name = "Ten";
				break;
			case 11:
				name = "Jack";
				break;
			case 12:
				name = "Queen";
				break;
			case 13:
				name = "King";
				break;
		}
	}
	
	public int getCard(int value, int suit) {
		int result = -1;
		if(suit == 0) {
			switch (value)
			{
				case 1:
					result = R.drawable.a_spades;
					break;
				case 2:
					result = R.drawable.two_spades;
					break;
				case 3:
					result = R.drawable.three_spades;
					break;
				case 4:
					result = R.drawable.four_spades;
					break;
				case 5:
					result = R.drawable.five_spades;
					break;
				case 6:
					result = R.drawable.six_spades;
					break;
				case 7:
					result = R.drawable.seven_spades;
					break;
				case 8:
					result = R.drawable.eight_spades;
					break;
				case 9:
					result = R.drawable.nine_spades;
					break;
				case 10:
					result = R.drawable.ten_spade;
					break;
				case 11:
					result = R.drawable.j_spades;
					break;
				case 12:
					result = R.drawable.q_spades;
					break;
				case 13:
					result = R.drawable.k_spades;
					break;
			}
			
		} else if (suit == 1) {
			switch (value)
			{
				case 1:
					result = R.drawable.a_clubs;
					break;
				case 2:
					result = R.drawable.two_clubs;
					break;
				case 3:
					result = R.drawable.three_clubs;
					break;
				case 4:
					result = R.drawable.four_clubs;
					break;
				case 5:
					result = R.drawable.five_clubs;
					break;
				case 6:
					result = R.drawable.six_clubs;
					break;
				case 7:
					result = R.drawable.seven_clubs;
					break;
				case 8:
					result = R.drawable.eight_clubs;
					break;
				case 9:
					result = R.drawable.nine_clubs;
					break;
				case 10:
					result = R.drawable.ten_clubs;
					break;
				case 11:
					result = R.drawable.j_clubs;
					break;
				case 12:
					result = R.drawable.q_clubs;
					break;
				case 13:
					result = R.drawable.k_clubs;
					break;
			}
		} else if (suit == 2) {
			switch (value)
			{
				case 1:
					result = R.drawable.a_hearts;
					break;
				case 2:
					result = R.drawable.two_hearts;
					break;
				case 3:
					result = R.drawable.three_hearts;
					break;
				case 4:
					result = R.drawable.four_hearts;
					break;
				case 5:
					result = R.drawable.five_hearts;
					break;
				case 6:
					result = R.drawable.six_hearts;
					break;
				case 7:
					result = R.drawable.seven_hearts;
					break;
				case 8:
					result = R.drawable.eight_hearts;
					break;
				case 9:
					result = R.drawable.nine_hearts;
					break;
				case 10:
					result = R.drawable.ten_heart;
					break;
				case 11:
					result = R.drawable.j_hearts;
					break;
				case 12:
					result = R.drawable.q_hearts;
					break;
				case 13:
					result = R.drawable.k_hearts;
					break;
			}
		} else if (suit == 3) {
			switch (value)
			{
				case 1:
					result = R.drawable.a_dia;
					break;
				case 2:
					result = R.drawable.two_dia;
					break;
				case 3:
					result = R.drawable.three_dia;
					break;
				case 4:
					result = R.drawable.four_dia;
					break;
				case 5:
					result = R.drawable.five_dia;
					break;
				case 6:
					result = R.drawable.six_dia;
					break;
				case 7:
					result = R.drawable.seven_dia;
					break;
				case 8:
					result = R.drawable.eight_dia;
					break;
				case 9:
					result = R.drawable.nine_dia;
					break;
				case 10:
					result = R.drawable.ten_dia;
					break;
				case 11:
					result = R.drawable.j_dia;
					break;
				case 12:
					result = R.drawable.q_dia;
					break;
				case 13:
					result = R.drawable.k_dia;
					break;
			}
		}
		return result;
	}
	
	// pre: value is between 0 and 3
	// post: suitname receives appropriate value dependent on value	
	private void assignSuit (int value)
	{
		switch (value)
		{
			case 0:
				suitname = "Spades";
				break;
			case 1:
				suitname = "Clubs";
				break;
			case 2:
				suitname = "Hearts";
				break;
			case 3:
				suitname = "Diamonds";
				break;
		}
	}
	
	// pre: value is between 0 and 3
	// post: suitname receives appropriate value dependent on value	
	public int getSuitValue(String value) {
		int result;
		if(value.equalsIgnoreCase("S")) {
			result = 0;
		} else if(value.equalsIgnoreCase("C")) {
			result = 1;			
		} else if(value.equalsIgnoreCase("H")) {
			result = 2;
		} else {
			result = 3;
		}
		return result;
	}
	
	// pre: none
	// post: returns value when method is called
	public int getValue ()
	{
		return value;
	}
	

	// pre: value is between 1 and 13
	// post: returns the appropriate name of the card dependent on value
	public String getName (int value)
	{
		assignName(value);
		return name;
	}
	
	// pre: value is between 1 and 13 
	// post: returns the appropriate suit of the card dependent on the value provided
	public String getSuit (int value)
	{
		assignSuit(value);
		return suitname;
	}
	
	// pre: none
	// post: returns the suit value which is between 0 and 3 that indicates what type of
	//       suit	
	public int getSuitVal()
	{
		return suit;
	}
}