package edu.handong.csee.plt;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.handong.csee.plt.ast.AST;
import edu.handong.csee.plt.ast.exception.FreeIdentifierException;

public class Main {
	
	private boolean onlyParser; // for -p option
	private String code;
	private String codeForParsing;
	
	public static void main(String[] args) {
		Main main = new Main();
		
		main.run(args);
	}
	
	private Options createOptions() {
		Options options = new Options();
		
		options.addOption(Option.builder("p") //parse
								.hasArg()
								.build());
		
		return options;
	}
	
	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			
			onlyParser = cmd.hasOption('p');
			codeForParsing = cmd.getOptionValue('p'); //get the code for only parsing
			
		} catch (ParseException e) {
			return false;
		}
		
		return true;
	}
	
	private void run(String[] args){
		Options options = createOptions();

		// Parser
		Parser parser = new Parser();
		// interpreter
		Interpreter interpreter = new Interpreter();
		AST ast;

		if(parseOptions(options, args)){
			if(onlyParser) {
				ast = parser.parse(codeForParsing);
				
				if(ast == null)
					System.out.println("Syntax Error!");
				
				System.out.println(ast.getASTCode()); //print out the code parsed
			}else {
				code = args[0];
				ast = parser.parse(code);
				
				if(ast == null)
					System.out.println("Syntax Error!");
				
				String result;
				
				try {
					result = interpreter.interp(ast);
					
					System.out.println(result); //print out the result
				} catch (FreeIdentifierException e) {
					System.out.println(e.getMessage());
				}
				
			}
			
		}
	}		
}
