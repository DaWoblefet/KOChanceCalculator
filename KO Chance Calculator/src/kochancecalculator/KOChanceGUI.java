package kochancecalculator;

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
import java.util.Scanner;

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

			GridPane.setConstraints(rollLabels.get(i), 0, i);
			GridPane.setConstraints(rollTFs.get(i), 1, i);

			rollsInput.getChildren().addAll(rollLabels.get(i), rollTFs.get(i));
		}

		Button addRowButton = new Button("+");
		GridPane.setConstraints(addRowButton, 0, 2);
		Button removeRowButton = new Button("-");
		removeRowButton.setPrefWidth(25);
		GridPane.setConstraints(removeRowButton, 1, 2);
		rollsInput.getChildren().addAll(addRowButton, removeRowButton);

		rollsInput.setVgap(2);
		pane.setCenter(rollsInput);

		/* Results, Bottom */
		GridPane results = new GridPane();

		Button calcButton = new Button("Calculate");
		GridPane.setConstraints(calcButton, 0, 0);

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

		GridPane.setConstraints(percentLabel, 0, 1);
		GridPane.setConstraints(percentTF, 1, 1);
		GridPane.setConstraints(copyPercent, 2, 1);
		GridPane.setConstraints(rawLabel, 0, 2);
		GridPane.setConstraints(rawTF, 1, 2);
		GridPane.setConstraints(copyRaw, 2, 2);
		GridPane.setConstraints(reducedLabel, 0, 3);
		GridPane.setConstraints(reducedTF, 1, 3);
		GridPane.setConstraints(copyReduced, 2, 3);

		results.getChildren().addAll(calcButton, percentLabel, percentTF, copyPercent, rawLabel, rawTF, copyRaw, reducedLabel, reducedTF, copyReduced);
		results.setHgap(5);
		pane.setBottom(results);

		addRowButton.setOnAction(e -> {
			rollLabels.add(new Label(" Set " + (rollLabels.size() + 1) + ": "));
			rollTFs.add(new TextField());

			int index = rollTFs.size() - 1;
			rollTFs.get(index).setPrefWidth(550);

			GridPane.setConstraints(rollLabels.get(index), 0, index);
			GridPane.setConstraints(rollTFs.get(index), 1, index);
			GridPane.setConstraints(addRowButton, 0, index + 1);
			GridPane.setConstraints(removeRowButton, 1, index + 1);

			rollsInput.getChildren().addAll(rollLabels.get(index), rollTFs.get(index));
		});

		removeRowButton.setOnAction(e -> {
			int index = rollTFs.size() - 1;

			rollsInput.getChildren().remove(rollLabels.get(index));
			rollsInput.getChildren().remove(rollTFs.get(index));

			rollLabels.remove(index);
			rollTFs.remove(index);

			GridPane.setConstraints(addRowButton, 0, index);
			GridPane.setConstraints(removeRowButton, 1, index);
		});

		calcButton.setOnAction(e -> {
			int HPStat = Integer.parseInt(hpTF.getText());

			int[][] damageRollSets = new int[rollTFs.size()][16];

			for (int i = 0; i < rollTFs.size(); i++)
			{
				damageRollSets[i] = parseDamageRolls(rollTFs.get(i).getText());
			}

			KOChanceLogic calculation = new KOChanceLogic(HPStat, damageRollSets);
			percentTF.setText(calculation.getPercentToKO());
			rawTF.setText(calculation.getFractionToKO());
			reducedTF.setText(calculation.getSimplifiedFractionToKO());
		});

		helpButton.setOnAction(e -> {openHelp();});
		copyPercent.setOnAction(e -> {copyToClipboard(percentTF.getText());});
		copyRaw.setOnAction(e -> {copyToClipboard(rawTF.getText());});
		copyReduced.setOnAction(e -> {copyToClipboard(reducedTF.getText());});

		Scene scene = new Scene(pane, 590, 300);
		primaryStage.setTitle("KO Chance Calculator");
		Image icon = new Image(getClass().getResourceAsStream("/resources/woblescientist.png"));
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

	public void copyToClipboard(String data)
	{
		content.putString(data);
		clipboard.setContent(content);
		return;
	}

	public void openHelp()
	{
		Stage stage = new Stage();
		String helpTextRaw = "";
		try
		{
			Scanner input = new Scanner((getClass().getResourceAsStream("/resources/helptext.txt")));
			while (input.hasNextLine())
			{
				helpTextRaw += input.nextLine() + "\n";
			}
			input.close();
		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
		TextArea helpText = new TextArea(helpTextRaw);
		helpText.setEditable(false);
		helpText.setWrapText(true);
		helpText.setPrefRowCount(19);

		Scene scene = new Scene(helpText, 500, 300);
		stage.setScene(scene);
		stage.setTitle("Help");
		stage.show();
		return;
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
