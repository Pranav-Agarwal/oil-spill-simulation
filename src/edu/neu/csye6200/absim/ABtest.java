package edu.neu.csye6200.absim;

public class ABtest {
	
    /**
     * Entry Point for the application. 
     * There is a single int launch parameter to set map size. (recommended 30 to 100)
     * If not specified, default map size is set to 50x50 cells.
     */
	public static void main(String[] args) {
		if(args.length>0) {
			new ABcontroller(Integer.parseInt(args[0]));
		}
		else {
			new ABcontroller();
		}
		
	}
}
