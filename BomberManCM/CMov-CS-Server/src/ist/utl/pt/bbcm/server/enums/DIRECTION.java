package ist.utl.pt.bbcm.server.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum DIRECTION {
	UP 		( 0,-1),
	DOWN	( 0, 1),
	LEFT	(-1, 0),
	RIGHT	( 1, 0);
	
	public int x;
	public int y;
	private static final List<DIRECTION> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();

	DIRECTION(int x, int y){
		this.x = x;
		this.y = y;
	}
	public static DIRECTION randomDir()  {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
}
