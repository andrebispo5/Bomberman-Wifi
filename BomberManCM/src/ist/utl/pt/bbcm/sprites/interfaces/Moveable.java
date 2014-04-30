package ist.utl.pt.bbcm.sprites.interfaces;

import ist.utl.pt.bbcm.enums.DIRECTION;

public interface Moveable {
	public void move(DIRECTION dir);
	public void moveRandom();
	public boolean canMove();
}
