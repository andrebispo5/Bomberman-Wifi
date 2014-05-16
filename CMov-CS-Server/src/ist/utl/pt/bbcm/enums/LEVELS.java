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
	LVL2(	"WWWWWWWWWWWWWWWWWWW\n"+ 
			"W-2-----R----O----W\n"+ 
			"WWOW-W-W-W-W-W-W-WW\n"+ 
			"W-O--O-----R------W\n"+ 
			"WWOWOW-W-W-W-WOW-WW\n"+ 
			"W--O-----1--O-R--3W\n"+ 
			"WW-W-W-W-W-W-W-W--W\n"+ 
			"W--O-O--O--O-O---OW\n"+ 
			"WWOW-WOWOW-W-W-W-WW\n"+ 
			"W--O-OO--R---W----W\n"+
			"WW-WRWOWOW-WOWOWOWW\n"+ 
			"W4------ROO-O-O-ORW\n"+
			"WWWWWWWWWWWWWWWWWWW"),
	LVL3(	"WWWWWWWWWWWWWWWWWWW\n"+ 
			"W--O--------O-R--3W\n"+ 
			"WWOW-W-W-W-W-W-W-WW\n"+ 
			"W-OOOO---1-------OW\n"+ 
			"WWOWOWOWOW-W-W-W-WW\n"+ 
			"W-2-----R----O----W\n"+ 
			"WWOW-W-W-W-W-W-W-WW\n"+ 
			"W-O--------R------W\n"+ 
			"WWOWOW-W-W-W-WOW-WW\n"+ 
			"W--OOOO--R--OOO---W\n"+ 
			"WW-W-WOWOW-WOWOWOWW\n"+ 
			"W-----4OOO--OO---RW\n"+ 
			"WWWWWWWWWWWWWWWWWWW");
	
	private String map;
	
	LEVELS(String mapStr){
		this.map = mapStr;
	}

	public String getMap() {
		return map;
	}	

}
