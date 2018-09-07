package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Demonstration program which gets a path for a Smart Script, and parses it 
 * into a tree, and reproduces its original for onto the standard output stream.
 * @author 0036502252
 *
 */
public class TreeWriter {
	/**
	 * Main method.
	 * @param args 1 argument expected; path to script file which will be parsed
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Expected a single argument - path to file.");
			return;
		}

		String docBody = "";
		try {
			docBody = new String(Files.readAllBytes(Paths.get(args[0])),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SmartScriptParser parser = null;
		try {
			parser = new SmartScriptParser(docBody);
		} catch (SmartScriptParserException e) {
			System.out.println("Unable to parse document!");
			System.exit(-1);
		} 
		
		WriterVisitor visitor = new WriterVisitor();
		System.out.println(docBody + "\n");
		parser.getDocumentNode().accept(visitor);
	}
	
	/**
	 * The visitor pattern used for printing the content of each node from the 
	 * recreated/parsed tree.
	 * @author 0036502252
	 *
	 */
	private static class WriterVisitor implements INodeVisitor{
		/** Used for appending and printing.*/
		private StringBuilder sb = new StringBuilder();

		@Override
		public void visitTextNode(TextNode node) {
			sb.append(node.toString());
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			sb.append("{$ FOR " + node.toString() + " $}");
			for (int i = 0, n = node.numberOfChildren(); i < n; i++) {
				Node child = node.getChild(i);
				if(child == null) continue;
				child.accept(this);
			}
			sb.append("{$END$}");
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			sb.append(node.toString());
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for (int i = 0, n = node.numberOfChildren(); i < n; i++) {
				Node child = node.getChild(i);
				if(child == null) continue;
				child.accept(this);
			}
			//print the end result
			System.out.println(sb.toString());
		}
	}
}
