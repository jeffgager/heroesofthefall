/**
 * 
 */
package com.hotf.server.dice;

import java.util.Vector;

/**
 * @author Jeff
 * 
 */
public class RollProcessor {

	private static RollProcessor INSTANCE;
	
	public static RollProcessor get() {
		if (INSTANCE == null) {
			INSTANCE = new RollProcessor();
		}
		return INSTANCE;
	}
	
	private RollProcessor() {
	}

	public String process(String in) {

		final String ina[] = in.split("\\[");

		if (ina.length < 2) {
			return in;
		}
		
		final StringBuffer b = new StringBuffer();
		for (String s : ina) {
			
			final int i = s.indexOf(']');
			
			if (i < 0) {
				
				b.append(s);
			
			} else {
				
				b.append("<b>[");
				b.append(roll(s.substring(0, i)));
				b.append("]</b>");

				if (s.length() > i) {
					b.append(s.substring(i + 1));
				}

			}

		}

		return b.toString();

	}

	private String roll(String s) {

		final Vector<DieRoll> v = DiceParser.parseRoll(s);

		if (v == null) {
			
			return "Roll Failed:" + s;
		
		} else {
			
			final StringBuffer b = new StringBuffer();
			b.append("Roll: " + s + ":");

			for (int i = 0; i < v.size(); i++) {
				DieRoll dr = v.get(i);
				b.append(v.get(i));
				b.append(": ");
				b.append(dr.makeRoll());
			}
			
			return b.toString();

		}

	}

}
