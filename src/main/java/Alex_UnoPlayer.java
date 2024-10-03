import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Alex_UnoPlayer implements UnoPlayer {
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
	public int play(List<Card> hand, Card upCard, Color calledColor, GameState state)
	{
		Color upColor = getUpColor(upCard, calledColor);
		int[] allowed = allowedCards(hand, upCard, calledColor);

		int highCard = hand.get(0).getNumber();
		int highCardIndex = 0;
		int wildCount = 0;
		if (allowed.length <= 0) {
			return -1;
		}

		for (int i : allowed) {
			Card card = hand.get(i);
			if (card.getRank() == Rank.WILD || card.getRank() == Rank.WILD_D4) {
				wildCount++;
			}
		}
		for (int i : allowed) {
			Card card = hand.get(i);
			if (card.getRank() == Rank.WILD || card.getRank() == Rank.WILD_D4) {
				wildCount++;
			}
		}
		for (int i : allowed) {
			Card card = hand.get(i);
			switch (card.getRank()) {
				case WILD_D4:
				case WILD:
					if (wildCount > 1) {
						return i;
					}
				case DRAW_TWO:
				case REVERSE:
				case SKIP:
					return i;
				case NUMBER:
					if (card.getNumber() > highCard) {
						highCard = card.getNumber();
						highCardIndex = i;
					}
				default:
					return i;
			}
		}
		return highCardIndex;
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
	public Color callColor(List<Card> hand) //literally just what color i have the most of
											//{{{
	{
		int reds = 0, greens = 0, yellows = 0, blues = 0;
		for (Card card : hand) {
			if (card.getColor() == Color.RED) {
				reds++;
			}
			if (card.getColor() == Color.YELLOW) {
				yellows++;
			}
			if (card.getColor() == Color.GREEN) {
				greens++;
			}
			if (card.getColor() == Color.BLUE) {
				blues++;
			}

		}
		if (reds >= yellows && reds >= greens && reds >= blues) {
			return UnoPlayer.Color.RED;
		}
		if (yellows >= reds && yellows >= greens && yellows >= blues) {
			return UnoPlayer.Color.YELLOW;
		}
		if (greens >= yellows && greens >= reds && greens >= blues) {
			return UnoPlayer.Color.GREEN;
		}
		if (blues >= reds && blues >= greens && blues >= yellows) {
			return UnoPlayer.Color.BLUE;
		}
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

	private int[] allowedCards(List<Card> hand, Card topCard, Color calledColor) {
		//took a long look at the docs for this one
		return IntStream.range(0, hand.size())
			.mapToObj((i) -> new Tuple<Integer, Card>(i, hand.get(i)))
			.filter((t) -> cardIsValid(t.value, topCard, calledColor))
			.mapToInt(t -> t.index)
			.toArray();
	}

	private boolean cardIsValid(Card card, Card topCard, Color calledColor) {
		if (getUpColor(topCard, calledColor) == card.getColor()) return true;

		switch (card.getRank()) {
			case WILD:
			case WILD_D4:
				return true;
			case NUMBER:
				return card.getNumber() == topCard.getNumber();
			case DRAW_TWO:
			case REVERSE:
			case SKIP:
				return card.getRank() == topCard.getRank();
			default:
				return false;
		}
	}

	public class Tuple<I, V> {
		public final I index;
		public final V value;
		public Tuple(I index, V value) {
			this.index = index;
			this.value = value;
		}
	}
}

class OtherPlayer {
	int score;
	int redsPlayed;
	int bluesPlayed;
	int yellowsPlayed;
	int greensPlayed;
	public OtherPlayer() {

	}
}
