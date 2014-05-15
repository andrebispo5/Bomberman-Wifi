package ist.utl.pt.bbcm.interfaces;

import ist.utl.pt.bbcm.enums.DIRECTION;

public interface Moveable {
	public void move(DIRECTION dir);
	public void moveRandom(DIRECTION direction);
	public boolean canMove();
}
