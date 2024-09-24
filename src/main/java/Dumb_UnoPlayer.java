import java.util.List;

public class Dumb_UnoPlayer implements UnoPlayer
{
	public int play(List<Card> hand, Card upCard,
			Color calledColor, GameState state)
	{
		// plays the first legal card
		for (int i = 0; i < hand.size(); i++)
		{
			if (canPlayOn(hand.get(i), upCard, calledColor))
			{
				return i;
			}
		}
		return -1;
	}

	public Color callColor(List<Card> hand)
	{
		// good old Red, nothing beats Red!
		return Color.RED;
	}

	public boolean canPlayOn(Card card, Card upCard,
			Color calledColor)
	{
		// is that card even legal to play?
		boolean result = false;
		result |= card.getRank() == Rank.WILD;
		result |= card.getRank() == Rank.WILD_D4;
		result |= card.getColor() == upCard.getColor();
		result |= card.getColor() == calledColor;
		result |= (card.getRank() == upCard.getRank() &&
				card.getRank() != Rank.NUMBER);
		result |= (card.getNumber() == upCard.getNumber() &&
				card.getRank() == Rank.NUMBER &&
				upCard.getRank() == Rank.NUMBER);
		return result;
	}
}
