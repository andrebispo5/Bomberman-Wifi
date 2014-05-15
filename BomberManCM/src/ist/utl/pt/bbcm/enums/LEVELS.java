package ist.utl.pt.bbcm.enums;

public enum LEVELS {
	LVL1(	"WWWWWWWWWWWWWWWWWWW=" +
			"W-2-----R----O----W=" +
			"WWOW-W-W-W-W-W-W-WW=" +
			"W-O--------R------W=" +
			"WWOWOW-W-W-W-WOW-WW=" +
			"W--O--------O-R--3W=" +
			"WWOW-W-W-W-W-W-W-WW=" +
			"W-OOOO---1-------OW=" +
			"WWOWOWOWOW-W-W-W-WW=" +
			"W--OOOO--R--OOO---W=" +
			"WW-W-WOWOW-WOWOWOWW=" +
			"W------OOO--OOO-ORW=" +
			"WWWWWWWWWWWWWWWWWWW",3),
	LVL2(	"WWWWWWWWWWWWWWWWWWW=" +
			"W-2----------O----W=" +
			"WWOW-W-W-W-W-W-W-WW=" +
			"W-O---------------W=" +
			"WWOWOW-W-W-W-WOW-WW=" +
			"W--O--------O-R--3W=" +
			"WWOW-W-W-W-W-W-W-WW=" +
			"W-OOOO---1-------OW=" +
			"WWOWOWOWOW-W-W-W-WW=" +
			"W--OOOO-----OOO---W=" +
			"WW-W-WOWOW-WOWOWOWW=" +
			"W------OOO--OOO-O-W=" +
			"WWWWWWWWWWWWWWWWWWW",3);
	
	private String map;
	private int maxP;
	
	LEVELS(String mapStr, int maxP){
		this.map = mapStr;
		this.maxP = maxP;
	}

	public String getMap() {
		return map;
	}	

	public int getMaxP() {
		return maxP;
	}	
}
