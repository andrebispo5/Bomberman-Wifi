package ist.utl.pt.bbcm.enums;

public enum LEVELS {
	LVL1(	"WWWWWWWWWWWWWWWWWWW\n" +
			"W-2-----R----O----W\n" +
			"WWOW-W-W-W-W-W-W-WW\n" +
			"W-O--------R------W\n" +
			"WWOWOW-W-W-W-WOW-WW\n" +
			"W--O--------O-R--3W\n" +
			"WWOW-W-W-W-W-W-W-WW\n" +
			"W-OOOO---1-------OW\n" +
			"WWOWOWOWOW-W-W-W-WW\n" +
			"W--OOOO--R--OOO---W\n" +
			"WW-W-WOWOW-WOWOWOWW\n" +
			"W------OOO--OOO-ORW\n" +
			"WWWWWWWWWWWWWWWWWWW"),
	LVL2(	"WWWWWWWWWWWWWWWWWWW\n" +
			"W-2----------O----W\n" +
			"WWOW-W-W-W-W-W-W-WW\n" +
			"W-O---------------W\n" +
			"WWOWOW-W-W-W-WOW-WW\n" +
			"W--O--------O-R--3W\n" +
			"WWOW-W-W-W-W-W-W-WW\n" +
			"W-OOOO---1-------OW\n" +
			"WWOWOWOWOW-W-W-W-WW\n" +
			"W--OOOO-----OOO---W\n" +
			"WW-W-WOWOW-WOWOWOWW\n" +
			"W------OOO--OOO-O-W\n" +
			"WWWWWWWWWWWWWWWWWWW");
	
	private String map;
	
	LEVELS(String mapStr){
		this.map = mapStr;
	}

	public String getMap() {
		return map;
	}	

}
