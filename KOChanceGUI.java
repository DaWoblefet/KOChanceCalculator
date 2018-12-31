/*Written by Leonard Craft III (DaWoblefet), with algorithm design by Ansel Blume (Stats).*/

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.geometry.Insets;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;

public class KOChanceGUI extends Application
{
	private final Clipboard clipboard = Clipboard.getSystemClipboard();
	private final ClipboardContent content = new ClipboardContent();

	@Override
	public void start(Stage primaryStage)
	{
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(5, 2, 5, 2));

		/* HP Stat, top */
		BorderPane top = new BorderPane();
		top.setPadding(new Insets(0, 0, 5, 0));
		GridPane hpInput = new GridPane();
		Label hpLabel = new Label(" HP Stat: ");
		TextField hpTF = new TextField("0");
		hpTF.setPrefWidth(40);
		hpInput.addRow(0, hpLabel, hpTF);
		top.setLeft(hpInput);

		Button helpButton = new Button("Help");
		top.setRight(helpButton);

		pane.setTop(top);

		/* Damage roll entry, center */
		GridPane rollsInput = new GridPane();

		ArrayList<Label> rollLabels = new ArrayList<Label>();
		ArrayList<TextField> rollTFs = new ArrayList<TextField>();

		for (int i = 0; i < 2; i++)
		{
			rollLabels.add(new Label(" Set " + (i + 1) + ": "));
			rollTFs.add(new TextField());
			rollTFs.get(i).setPrefWidth(550);

			rollsInput.setConstraints(rollLabels.get(i), 0, i);
			rollsInput.setConstraints(rollTFs.get(i), 1, i);

			rollsInput.getChildren().addAll(rollLabels.get(i), rollTFs.get(i));
		}

		Button addRowButton = new Button("+");
		rollsInput.setConstraints(addRowButton, 0, 2);
		Button removeRowButton = new Button("-");
		removeRowButton.setPrefWidth(25);
		rollsInput.setConstraints(removeRowButton, 1, 2);
		rollsInput.getChildren().addAll(addRowButton, removeRowButton);

		rollsInput.setVgap(2);
		pane.setCenter(rollsInput);

		/* Results, Bottom */
		GridPane results = new GridPane();

		Button calcButton = new Button("Calculate");
		results.setConstraints(calcButton, 0, 0);

		Label percentLabel = new Label(" Percent of the time:");
		TextField percentTF = new TextField();
		percentTF.setEditable(false);
		percentTF.setPrefWidth(200);
		Button copyPercent = new Button("Copy");

		Label rawLabel = new Label(" Raw fraction:");
		TextField rawTF = new TextField();
		rawTF.setEditable(false);
		Button copyRaw = new Button("Copy");

		Label reducedLabel = new Label(" Simplified fraction:");
		TextField reducedTF = new TextField();
		reducedTF.setEditable(false);
		Button copyReduced = new Button("Copy");

		results.setConstraints(percentLabel, 0, 1);
		results.setConstraints(percentTF, 1, 1);
		results.setConstraints(copyPercent, 2, 1);
		results.setConstraints(rawLabel, 0, 2);
		results.setConstraints(rawTF, 1, 2);
		results.setConstraints(copyRaw, 2, 2);
		results.setConstraints(reducedLabel, 0, 3);
		results.setConstraints(reducedTF, 1, 3);
		results.setConstraints(copyReduced, 2, 3);

		results.getChildren().addAll(calcButton, percentLabel, percentTF, copyPercent, rawLabel, rawTF, copyRaw, reducedLabel, reducedTF, copyReduced);
		results.setHgap(5);
		pane.setBottom(results);

		addRowButton.setOnAction(e -> {
			rollLabels.add(new Label(" Set " + (rollLabels.size() + 1) + ": "));
			rollTFs.add(new TextField());

			int index = rollTFs.size() - 1;
			rollTFs.get(index).setPrefWidth(550);

			rollsInput.setConstraints(rollLabels.get(index), 0, index);
			rollsInput.setConstraints(rollTFs.get(index), 1, index);
			rollsInput.setConstraints(addRowButton, 0, index + 1);
			rollsInput.setConstraints(removeRowButton, 1, index + 1);

			rollsInput.getChildren().addAll(rollLabels.get(index), rollTFs.get(index));
		});

		removeRowButton.setOnAction(e -> {
			int index = rollTFs.size() - 1;

			rollsInput.getChildren().remove(rollLabels.get(index));
			rollsInput.getChildren().remove(rollTFs.get(index));

			rollLabels.remove(index);
			rollTFs.remove(index);

			rollsInput.setConstraints(addRowButton, 0, index);
			rollsInput.setConstraints(removeRowButton, 1, index);
		});

		calcButton.setOnAction(e -> {
			int HPStat = Integer.parseInt(hpTF.getText());


			int[][] damageRollSets = new int[rollTFs.size()][16];

			for (int i = 0; i < rollTFs.size(); i++)
			{
				damageRollSets[i] = parseDamageRolls(rollTFs.get(i).getText());
			}

			int rawCount = getKOChance(HPStat, damageRollSets);
			int possibleCombinations = (int) Math.pow(16, rollTFs.size());
			percentTF.setText(100.0 * rawCount / possibleCombinations + "% chance to KO");
			rawTF.setText(rawCount + "/" + possibleCombinations);
			reducedTF.setText(reduceFraction(rawCount, possibleCombinations));
		});

		helpButton.setOnAction(e -> {openHelp();});
		copyPercent.setOnAction(e -> {copyToClipboard(percentTF.getText());});
		copyRaw.setOnAction(e -> {copyToClipboard(rawTF.getText());});
		copyReduced.setOnAction(e -> {copyToClipboard(reducedTF.getText());});

		Scene scene = new Scene(pane, 590, 300);
		primaryStage.setTitle("KO Chance Calculator");
		Image icon = new Image(KOChanceGUI.class.getResourceAsStream("woblescientist.png"));
		primaryStage.getIcons().add(icon);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public int[] parseDamageRolls(String input)
	{
		//Sanitizes () if they exist.
		if (input.contains("("))
		{
			input = input.substring(1, input.length());
		}
		if (input.contains(")"))
		{
			input = input.substring(0, input.length() - 1);
		}
		String[] rollsString = input.split(", ");

		//Sanity check for damage roll input.
		if (rollsString.length != 16)
		{
			System.out.println("Bad input");
			return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		}

		//If you've made it this far, you should be good.
		int[] damageRolls = new int[16];
		for (int i = 0; i < 16; i++)
		{
			damageRolls[i] = Integer.parseInt(rollsString[i]);
		}

		return damageRolls;
	}

	public int getKOChance(int HPStat, int[][] dmgRolls)
	{
		//Turns all the sets of damage rolls into a big arraylist.
		ArrayList<Integer> combinedRolls = new ArrayList<Integer>();

		for (int i = 0; i < dmgRolls[0].length; i++)
		{
			combinedRolls.add(dmgRolls[0][i]);
		}

		int result = getKOChance(HPStat, combinedRolls, 1, dmgRolls);
		return result;
	}

	//Generates all combinations of possible damage rolls recursively and compares each against an HP stat. Blame Stats if the algorithm's bad.
	public int getKOChance(int HPStat, ArrayList<Integer> combinedRolls, int beginIndex, int[][] dmgRolls)
	{

		if (beginIndex >= dmgRolls.length)
		{
			int count = 0;

			for (int i = 0; i < combinedRolls.size(); i++)
			{
				if (combinedRolls.get(i) >= HPStat) count++;
			}

			System.out.println(100.0 * count / combinedRolls.size() + "% chance to KO");
			System.out.println("Fraction equivalent: " + reduceFraction(count, combinedRolls.size()));
			return count;
		}

		ArrayList<Integer> newRolls = new ArrayList<Integer>();

		for (int i = 0; i < combinedRolls.size(); i++)
		{
			for (int j = 0; j < dmgRolls[beginIndex].length; j++)
			{
				newRolls.add(combinedRolls.get(i) + dmgRolls[beginIndex][j]);
			}
		}

		return getKOChance(HPStat, newRolls, beginIndex + 1, dmgRolls);
	}

	public String reduceFraction(int top, int bottom)
	{
		int gcd = getGCD(top, bottom);
		return top / gcd + "/" + bottom / gcd;
	}

	public int getGCD(int top, int bottom)
	{
		if (bottom == 0) {return top;}
		return getGCD(bottom, top%bottom);
	}

	public void copyToClipboard(String data)
	{
		content.putString(data);
		clipboard.setContent(content);
		return;
	}

	public void openHelp()
	{
		Stage stage = new Stage();
		TextArea helpText = new TextArea("KO Chance Calculator, written by DaWoblefet. Special thanks to Stats for algorithm design.\n\nThis program is used to calculate the chance of a combination attack KOing a Pokemon.\n\nSteps:\n1) Input the HP stat of the target Pokemon.\n2) Copy and paste the damage rolls for each of your attacks from the damage calculator. If needed, use the \"+\" or \"-\" buttons to add or remove sets of damage rolls as necessary.\n3) Press Calculate.\n\nNOTE: if you add a lot of damage rolls (like more than 5), the program may lag and run more slowly, but it's working.");
		helpText.setEditable(false);
		helpText.setWrapText(true);
		helpText.setPrefRowCount(19);

		Scene scene = new Scene(helpText, 500, 300);
		stage.setScene(scene);
		stage.setTitle("Help");
		stage.show();
		return;
	}
}
