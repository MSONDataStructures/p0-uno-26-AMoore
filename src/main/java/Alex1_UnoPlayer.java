import java.util.ArrayList;
import java.util.List;

public class Alex1_UnoPlayer implements UnoPlayer {
	/** {{{
	 * play - This method is called when it's your turn and you need to
	 * choose what card to play.
	 *
	 * The hand parameter tells you what's in your hand. You can call
	 * getColor(), getRank(), and getNumber() on each of the cards it
	 * contains to see what it is. The color will be the color of the card,
	 * or "Color.NONE" if the card is a wild card. The rank will be
	 * "Rank.NUMBER" for all numbered cards, and another value (e.g.,
	 * "Rank.SKIP," "Rank.REVERSE," etc.) for special cards. The value of
	 * a card's "number" only has meaning if it is a number card. 
	 * (Otherwise, it will be -1.)
	 *
	 * The upCard parameter works the same way, and tells you what the 
	 * up card (in the middle of the table) is.
	 * The calledColor parameter only has meaning if the up card is a wild,
	 * and tells you what color the player who played that wild card called.
	 *
	 * Finally, the state parameter is a GameState object on which you can 
	 * invoke methods if you choose to access certain detailed information
	 * about the game (like who is currently ahead, what colors each player
	 * has recently called, etc.)
	 *
	 * You must return a value from this method indicating which card you
	 * wish to play. If you return a number 0 or greater, that means you
	 * want to play the card at that index. If you return -1, that means
	 * that you cannot play any of your cards (none of them are legal plays)
	 * in which case you will be forced to draw a card (this will happen
	 * automatically for you.)
	 * }}}
	 */
	boolean redAllowed = true;
	boolean yellowAllowed = true;
	boolean greenAllowed = true;
	boolean blueAllowed = true;

	public int play(List<Card> hand, Card upCard, Color calledColor, GameState state)
	{
		Color upColor = getUpColor(upCard, calledColor);
		List<Throuple<Integer, Card, Double>> cards = findAllowedCards(hand, upColor, upCard.getNumber(), upCard.getRank());
		if (cards.size() > 0) {
			cards.iterator().forEachRemaining((card) -> {card.weight = weigh(card, upColor, state);});
			Throuple<Integer, Card, Double> highCard = cards.get(0);
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).weight > highCard.weight) {
					highCard = cards.get(i);
				}
			}
			return highCard.index;
		} else {
			return -1;
		}
	}
	private Double weigh(Throuple<Integer, Card, Double> card, UnoPlayer.Color upColor, GameState state) {
		double value = 0;
		switch (card.value.getRank()) {
			case WILD:
			case WILD_D4:
				value -= 20;
			case DRAW_TWO:
			case REVERSE:
			case SKIP:
				value += 20;
			case NUMBER:
				value += card.value.getNumber();
		}
		if (state.getNumCardsInHandsOfUpcomingPlayers()[0] <= 3) {
			switch (card.value.getRank()) {
				case WILD:
					value += 20;
				case WILD_D4:
					value += 50;
				case DRAW_TWO:
				case REVERSE:
				case SKIP:
					value += 50;
				case NUMBER:
					value -= 100;
			}
		}
		//add some pizzaz
		value += (Math.random() - .5) * 1000;
		return value;
	}
	public void handleColor(GameState state) {
		redAllowed = true;
		yellowAllowed = true;
		greenAllowed = true;
		blueAllowed = true;
		for (int i = 0; i < state.getNumCardsInHandsOfUpcomingPlayers().length; i++) {
			if (state.getNumCardsInHandsOfUpcomingPlayers()[i] <= 3) {
				if (state.getMostRecentColorCalledByUpcomingPlayers()[i] == null)
					break;
				switch (state.getMostRecentColorCalledByUpcomingPlayers()[i]) {
					case BLUE:
						blueAllowed = false;
					case GREEN:
						greenAllowed = false;
					case RED:
						redAllowed = false;
					case YELLOW:
						yellowAllowed = false;
					default:
						break;
				}
			}
		}
	}

	/** {{{
	 * callColor - This method will be called when you have just played a
	 * wild card, and is your way of specifying which color you want to 
	 * change it to.
	 *
	 * You must return a valid Color value from this method. You must not
	 * return the value Color.NONE under any circumstances.
	 * }}}
	 */
	public Color callColor(List<Card> hand) //literally just what color i have the most of and screws the other people
											//{{{
	{
		int reds = 0, greens = 0, yellows = 0, blues = 0;

		for (Card card : hand) {
			if (card.getColor() == Color.RED && redAllowed) {
				reds++;
			}
			if (card.getColor() == Color.YELLOW && yellowAllowed) {
				yellows++;
			}
			if (card.getColor() == Color.GREEN && greenAllowed) {
				greens++;
			}
			if (card.getColor() == Color.BLUE && blueAllowed) {
				blues++;
			}
		}

		if (reds >= yellows && reds >= greens && reds >= blues) {
			// i know this can all go in one `if` but its prettier this way imo
			if (redAllowed) {
				return UnoPlayer.Color.RED;
			}
		}
		if (yellows >= reds && yellows >= greens && yellows >= blues) {
			if (yellowAllowed) {
				return UnoPlayer.Color.YELLOW;
			}
		}
		if (greens >= yellows && greens >= reds && greens >= blues) {
			if (greenAllowed) {
				return UnoPlayer.Color.GREEN;
			}
		}
		if (blues >= reds && blues >= greens && blues >= yellows) {
			if (blueAllowed) {
				return UnoPlayer.Color.BLUE;
			}
		}
		// Good old Red
		return UnoPlayer.Color.RED;
	}
	//}}}

	private UnoPlayer.Color getUpColor(Card upCard, UnoPlayer.Color calledColor) {
		if (calledColor == Color.NONE) {
			return upCard.getColor();
		} else {
			return calledColor;
		}
	}

	private List<Throuple<Integer, Card, Double>> findAllowedCards(List<Card> hand, Color color, int card, Rank rank) {
		List<Throuple<Integer, Card, Double>> allowedCards = new ArrayList<Throuple<Integer, Card, Double>>();
		for (int i = 0; i < hand.size(); i++) {
			Card checkCard = hand.get(i);
			double weight = 0;
			if (checkCard.getRank() != Rank.NUMBER && checkCard.getRank() == rank || checkCard.getRank() == Rank.WILD || checkCard.getRank() == Rank.WILD_D4) {
				allowedCards.add(this.new Throuple<>(i, checkCard, weight));
			} else if (checkCard.getRank() == Rank.NUMBER && checkCard.getNumber() == card) {
				allowedCards.add(this.new Throuple<>(i, checkCard, weight));
			} else if (checkCard.getColor() == color) {
				allowedCards.add(this.new Throuple<>(i, checkCard, weight));
			}
		}
		return allowedCards;
	}

	// looking back, probably unnessaccary, but i really like ~~touples~~ throuples, and I have been using rust a little too much maybe
	public class Throuple<I, V, W> {
		public I index;
		public V value;
		public W weight;
		public Throuple(I index, V value, W weight) {
			this.index = index;
			this.value = value;
			this.weight = weight;
		}
	}
	class OtherPlayer {
		int score;
		int redsPlayed;
		int bluesPlayed;
		int yellowsPlayed;
		int greensPlayed;

		public OtherPlayer(GameState state) {
			state.getPlayedCards();
		}
	}
}
