package com.hotf.client.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.image.Images;
import com.hotf.client.view.control.AccountOracle;
import com.hotf.client.view.control.Chooser;
import com.hotf.client.view.control.HotfCellBrowser;
import com.hotf.client.view.control.ImageButtonCell;
import com.hotf.client.view.control.RichTextEditor;

public class CharacterEditImpl extends Composite implements CharacterEdit {

	private static final int[][] SKILL_LEVEL_BY_RANK_COST = {
		{0, 0, 0, 0, 1, 1, 1, 1, 2},
		{1, 1, 1, 1, 2, 2, 2, 2, 3},
		{1, 1, 2, 2, 3, 3, 4, 4, 5},
		{1, 1, 2, 3, 4, 4, 5, 6, 7},
		{1, 2, 3, 4, 5, 6, 7, 8, 9},
		{2, 3, 4, 5, 6, 7, 8, 9, 9},
		{3, 4, 5, 6, 7, 8, 9, 9, 9}
		};

	private static final int[][] SKILL_MODIFIER_BY_RANK_COST = {
		{99, 75, 50, 25, 99, 75, 50, 25, 99},
		{99, 75, 50, 25, 99, 75, 50, 25, 99},
		{99, 50, 99, 50, 99, 50, 99, 50, 99},
		{99, 25, 50, 75, 99, 25, 50, 75, 99},
		{99, 99, 99, 99, 99, 99, 99, 99, 99},
		{99, 99, 99, 99, 99, 99, 99, 99, 99},
		{99, 99, 99, 99, 99, 99, 99, 99, 99}
		};
	
	public interface BrowserCellStyles extends CellBrowser.Style {
		String editableCell();
	}
	public interface BrowserCellResources extends CellBrowser.Resources {
	    @Source({"browser.css"})
	    BrowserCellStyles cellBrowserStyle();
	}
	private BrowserCellResources browserResources = (BrowserCellResources) GWT.create(BrowserCellResources.class);

	public interface TableCellResources extends CellTable.Resources {
	    @Source({CellTable.Style.DEFAULT_CSS, "table.css"})
	    Style cellTableStyle();
	}
	private TableCellResources tableResources = (TableCellResources) GWT.create(TableCellResources.class);

	private Images images = GWT.create(Images.class); 

	private static final String[] TARGET_AREAS = { "Head ", "Left Eye<br/>",
			"Right Eye<br/>", "Face ", "Neck ", "Throat ",
			"Left Shoulder<br/>", "Right Shoulder<br/>", "Left Arm<br/>",
			"Right Arm<br/>", "Left Hand<br/>", "Right Hand<br/>",
			"Chest<br/>", "Abdomen<br/>", "Buttock<br/>", "Groin<br/>",
			"Left Thigh<br/>", "Right Thigh<br/>", "Left Leg<br/>",
			"Right Leg<br/>", "Left Foot<br/>", "Right Foot<br/>", "Back " };

	private static CharacterEditImplUiBinder uiBinder = GWT
			.create(CharacterEditImplUiBinder.class);

	interface CharacterEditImplUiBinder extends
			UiBinder<Widget, CharacterEditImpl> {
	}

	private NumberFormat zz = NumberFormat.getFormat("00");
	private Presenter presenter;

	@UiField(provided = true) final Resources resources;
	@UiField TextBox nameField;
	@UiField TextBox statusField;
	@UiField(provided = true) Chooser playerField;
	@UiField(provided = true) RichTextEditor descriptionField;
	@UiField(provided = true) RichTextEditor sheetField;
	@UiField TextBox wyrdField;
	@UiField ListBox handedField;
	@UiField ListBox skillRanksField;
	@UiField TextBox characterPointsField;
	@UiField Label characterCostField;
	@UiField TextBox vigourField;
	@UiField TextBox mettleField;
	@UiField TextBox witField;
	@UiField TextBox glamourField;
	@UiField TextBox spiritField;
	@UiField Image portraitField;
	@UiField Label skillCostField;

	@UiField ListBox weaponSelection;
	@UiField ListBox armourSelection;
	@UiField ListBox artifactSelection;
	@UiField ListBox generalSkillSelection;
	@UiField ListBox skillSelection;

	private ListDataProvider<CharacterGeneralSkillResult> generalSkillDataProvider;
	private ListDataProvider<CharacterSkillResult> skillDataProvider;
	@UiField(provided = true) HotfCellBrowser skillsList;
	private ListDataProvider<GameWeaponResult> weaponDataProvider;
	@UiField(provided = true) CellTable<GameWeaponResult> weaponList;
	private ListDataProvider<GameArmourResult> armourDataProvider;
	@UiField(provided = true) CellTable<GameArmourResult> armourList;
	private ListDataProvider<GameArtifactResult> artifactDataProvider;
	@UiField(provided = true) CellTable<GameArtifactResult> artifactList;

	@UiField Anchor headLink;
	@UiField Anchor faceLink;
	@UiField Anchor neckLink;
	@UiField Anchor throatLink;
	@UiField Anchor leftEyeLink;
	@UiField Anchor rightEyeLink;
	@UiField Anchor leftShoulderLink;
	@UiField Anchor rightShoulderLink;
	@UiField Anchor leftArmLink;
	@UiField Anchor rightArmLink;
	@UiField Anchor leftHandLink;
	@UiField Anchor rightHandLink;
	@UiField Anchor chestLink;
	@UiField Anchor abdomenLink;
	@UiField Anchor buttockLink;
	@UiField Anchor groinLink;
	@UiField Anchor leftThighLink;
	@UiField Anchor rightThighLink;
	@UiField Anchor leftLegLink;
	@UiField Anchor rightLegLink;
	@UiField Anchor leftFootLink;
	@UiField Anchor rightFootLink;
	@UiField Anchor backLink;
	@UiField ListBox replacementArmourField;

	@UiField HorizontalPanel setArmourCommands;
	@UiField HorizontalPanel addArmourCommands;
	@UiField HorizontalPanel addWeaponCommands;
	@UiField HorizontalPanel addArtifactCommands;
	@UiField HorizontalPanel addGeneralSkillCommands;
	@UiField HorizontalPanel addSkillCommands;
	@UiField TabLayoutPanel tabPanel;
	
	private SingleSelectionModel<CharacterGeneralSkillResult> generalSkillSelectionModel;
	private SingleSelectionModel<CharacterSkillResult> skillSelectionModel;

	public CharacterEditImpl(ClientFactory clientFactory) {

		super();
		this.resources = clientFactory.getResources();
		this.descriptionField = clientFactory.createRichTextEditor();
		this.sheetField = clientFactory.createRichTextEditor();

		AccountOracle accountOracle = new AccountOracle(clientFactory);
		playerField = new Chooser(accountOracle);

		weaponDataProvider = new ListDataProvider<GameWeaponResult>();
		weaponList = new CellTable<GameWeaponResult>(weaponDataProvider.getList().size(), tableResources);
		weaponList.setWidth("100%", true);
		weaponDataProvider.addDataDisplay(weaponList);
		weaponList.addColumn(new Column<GameWeaponResult, String>(new TextCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				return w.getName();
			}
		}, "Weapons");
		IdentityColumn<GameWeaponResult> removeWeapon = new IdentityColumn<GameWeaponResult>(new ImageButtonCell<GameWeaponResult>("Remove", images.deleteobj()) {
			@Override
			public void execute(GameWeaponResult value) {
				presenter.deleteWeapon(value);
			}
		});
		weaponList.addColumn(removeWeapon);
		weaponList.setColumnWidth(removeWeapon, "36px");

		armourDataProvider = new ListDataProvider<GameArmourResult>();
		armourList = new CellTable<GameArmourResult>(armourDataProvider.getList().size(), tableResources);
		armourList.setWidth("100%", true);
		armourDataProvider.addDataDisplay(armourList);

		armourList.addColumn(new Column<GameArmourResult, String>(new TextCell()) {
			@Override
			public String getValue(GameArmourResult w) {
				return w.getName();
			}
		}, "Armour");
		IdentityColumn<GameArmourResult> removeArmour = new IdentityColumn<GameArmourResult>(new ImageButtonCell<GameArmourResult>("Remove", images.deleteobj()) {
			@Override
			public void execute(GameArmourResult value) {
				presenter.deleteArmour(value);
			}
		});
		armourList.addColumn(removeArmour);
		armourList.setColumnWidth(removeArmour, "36px");

		artifactDataProvider = new ListDataProvider<GameArtifactResult>();
		artifactList = new CellTable<GameArtifactResult>(artifactDataProvider.getList().size(), tableResources);
		artifactList.setWidth("100%", true);
		artifactDataProvider.addDataDisplay(artifactList);

		artifactList.addColumn(new Column<GameArtifactResult, String>(new TextCell()) {
			@Override
			public String getValue(GameArtifactResult w) {
				return w.getName();
			}
		}, "Artifact");
		IdentityColumn<GameArtifactResult> removeArtifact = new IdentityColumn<GameArtifactResult>(new ImageButtonCell<GameArtifactResult>("Remove", images.deleteobj()) {
			@Override
			public void execute(GameArtifactResult value) {
				presenter.deleteArtifact(value);
			}
		});
		artifactList.addColumn(removeArtifact);
		artifactList.setColumnWidth(removeArtifact, "36px");

		generalSkillDataProvider = new ListDataProvider<CharacterGeneralSkillResult>();
		generalSkillSelectionModel = new SingleSelectionModel<CharacterGeneralSkillResult>();
		generalSkillSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						presenter.selectGeneralSkill(generalSkillSelectionModel
								.getSelectedObject());
						skillSelectionModel.setSelected(
								skillSelectionModel.getSelectedObject(), false);
						presenter.selectSkill(null);
					}
				});
		skillSelectionModel = new SingleSelectionModel<CharacterSkillResult>();
		skillSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						presenter.selectSkill(skillSelectionModel
								.getSelectedObject());
					}
				});
		skillsList = new HotfCellBrowser(new SkillTreeModel(), null, browserResources);
		skillsList.setWidth("100%");
		skillsList.setHeight("390px");
		skillsList.setDefaultColumnWidth(347);
		skillsList.setMinimumColumnWidth(347);
		SplitLayoutPanel splitPanel = skillsList.getWidget();
		splitPanel.setWidgetSize(splitPanel.getWidget(0), 347.0D);

		initWidget(uiBinder.createAndBindUi(this));

		portrait = new HasImageImpl(portraitField);
		portraitHidden = new HasVisibilityImpl(portraitField);

		BlurHandler blurHandler = new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				presenter.validate();
				generalSkillDataProvider.refresh();
			}
		};

		nameField.addBlurHandler(blurHandler);
		statusField.addBlurHandler(blurHandler);
		wyrdField.addBlurHandler(blurHandler);
		handedField.addBlurHandler(blurHandler);
		skillRanksField.addBlurHandler(blurHandler);
		characterPointsField.addBlurHandler(blurHandler);
		vigourField.addBlurHandler(blurHandler);
		mettleField.addBlurHandler(blurHandler);
		witField.addBlurHandler(blurHandler);
		glamourField.addBlurHandler(blurHandler);
		spiritField.addBlurHandler(blurHandler);

		ClickHandler selectArmourHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object src = event.getSource();
				if (src == headLink) {
					selectedTarget = 0;
				} else if (src == leftEyeLink) {
					selectedTarget = 1;
				} else if (src == rightEyeLink) {
					selectedTarget = 2;
				} else if (src == faceLink) {
					selectedTarget = 3;
				} else if (src == neckLink) {
					selectedTarget = 4;
				} else if (src == throatLink) {
					selectedTarget = 5;
				} else if (src == leftShoulderLink) {
					selectedTarget = 6;
				} else if (src == rightShoulderLink) {
					selectedTarget = 7;
				} else if (src == leftArmLink) {
					selectedTarget = 8;
				} else if (src == rightArmLink) {
					selectedTarget = 9;
				} else if (src == leftHandLink) {
					selectedTarget = 10;
				} else if (src == rightHandLink) {
					selectedTarget = 11;
				} else if (src == chestLink) {
					selectedTarget = 12;
				} else if (src == abdomenLink) {
					selectedTarget = 13;
				} else if (src == buttockLink) {
					selectedTarget = 14;
				} else if (src == groinLink) {
					selectedTarget = 15;
				} else if (src == leftThighLink) {
					selectedTarget = 16;
				} else if (src == rightThighLink) {
					selectedTarget = 17;
				} else if (src == leftLegLink) {
					selectedTarget = 18;
				} else if (src == rightLegLink) {
					selectedTarget = 19;
				} else if (src == leftFootLink) {
					selectedTarget = 20;
				} else if (src == rightFootLink) {
					selectedTarget = 21;
				} else if (src == backLink) {
					selectedTarget = 22;
				}
				presenter.updateArmourSelection(selectedTarget, 
					Integer.parseInt(replacementArmourField.getValue(replacementArmourField.getSelectedIndex())));
			}
		};
		headLink.addClickHandler(selectArmourHandler);
		leftEyeLink.addClickHandler(selectArmourHandler);
		rightEyeLink.addClickHandler(selectArmourHandler);
		faceLink.addClickHandler(selectArmourHandler);
		neckLink.addClickHandler(selectArmourHandler);
		throatLink.addClickHandler(selectArmourHandler);
		leftShoulderLink.addClickHandler(selectArmourHandler);
		rightShoulderLink.addClickHandler(selectArmourHandler);
		leftArmLink.addClickHandler(selectArmourHandler);
		rightArmLink.addClickHandler(selectArmourHandler);
		leftHandLink.addClickHandler(selectArmourHandler);
		rightHandLink.addClickHandler(selectArmourHandler);
		chestLink.addClickHandler(selectArmourHandler);
		abdomenLink.addClickHandler(selectArmourHandler);
		buttockLink.addClickHandler(selectArmourHandler);
		groinLink.addClickHandler(selectArmourHandler);
		leftThighLink.addClickHandler(selectArmourHandler);
		rightThighLink.addClickHandler(selectArmourHandler);
		leftLegLink.addClickHandler(selectArmourHandler);
		rightLegLink.addClickHandler(selectArmourHandler);
		leftFootLink.addClickHandler(selectArmourHandler);
		rightFootLink.addClickHandler(selectArmourHandler);
		backLink.addClickHandler(selectArmourHandler);

	}

	@Override
	public void setShowTabs(boolean value) {
		tabPanel.setVisible(value);
	}

	private int selectedTarget = 0;

	@Override
	public int getSelectedTarget() {
		return selectedTarget;
	}

	private Anchor getTarget(int idx) {
		switch (idx) {
		case 0:
			return headLink;
		case 1:
			return leftEyeLink;
		case 2:
			return rightEyeLink;
		case 3:
			return faceLink;
		case 4:
			return neckLink;
		case 5:
			return throatLink;
		case 6:
			return leftShoulderLink;
		case 7:
			return rightShoulderLink;
		case 8:
			return leftArmLink;
		case 9:
			return rightArmLink;
		case 10:
			return leftHandLink;
		case 11:
			return rightHandLink;
		case 12:
			return chestLink;
		case 13:
			return abdomenLink;
		case 14:
			return buttockLink;
		case 15:
			return groinLink;
		case 16:
			return leftThighLink;
		case 17:
			return rightThighLink;
		case 18:
			return leftLegLink;
		case 19:
			return rightLegLink;
		case 20:
			return leftFootLink;
		case 21:
			return rightFootLink;
		case 22:
			return backLink;
		default:
			return null;
		}

	}

	@Override
	public void setArmour(String armourType[], Integer[] slash, Integer[] crush, Integer[] pierce) {
		for (int i = 0; i < slash.length; i++) {
			setTargetArmour(armourType[i], getTarget(i), TARGET_AREAS[i],
					presenter.integerToString(slash[i] != null ? slash[i] : 0),
					presenter.integerToString(crush[i] != null ? crush[i] : 0),
					presenter.integerToString(pierce[i] != null ? pierce[i] : 0));
		}
	}

	private void setTargetArmour(String armourType, HasHTML w, String name, String s, String c, String p) {
		w.setHTML(name + " " + armourType + " (" + s + "/" + c + "/" + p + ")");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		generalSkillSelectionModel.setSelected(
				generalSkillSelectionModel.getSelectedObject(), false);
		skillSelectionModel.setSelected(
				skillSelectionModel.getSelectedObject(), false);
	}

	@Override
	public HasSelectionHandlers<Suggestion> getPlayerSelection() {
		return playerField;
	}

	@Override
	public HasText getPlayerField() {
		return playerField;
	}

	@Override
	public HasEnabled getPlayerEnabled() {
		return playerField.getTextBox();
	}

	@Override
	public HasText getNameField() {
		return nameField;
	}

	@Override
	public Focusable getNameFocus() {
		return nameField;
	}

	@Override
	public HasText getStatusField() {
		return statusField;
	}

	private HasImage portrait;

	@Override
	public HasImage getPortraitField() {
		return portrait;
	}

	private HasVisibility portraitHidden;

	@Override
	public HasVisibility getPortraitHidden() {
		return portraitHidden;
	}

	@Override
	public HasHTML getDescriptionField() {
		return descriptionField;
	}

	@Override
	public HasHTML getSheetField() {
		return sheetField;
	}

	@Override
	public HasText getCharacterPointsField() {
		return characterPointsField;
	}

	@Override
	public HasText getVigourField() {
		return vigourField;
	}

	@Override
	public HasText getMettleField() {
		return mettleField;
	}

	@Override
	public HasText getWitField() {
		return witField;
	}

	@Override
	public HasText getGlamourField() {
		return glamourField;
	}

	@Override
	public HasText getSpiritField() {
		return spiritField;
	}

	@Override
	public HasText getSkillCostField() {
		return skillCostField;
	}

	@UiHandler("closeLink")
	public void close(ClickEvent e) {
		presenter.close();
	}

	@UiHandler("saveLink")
	public void save(ClickEvent e) {
		presenter.save();
	}

	@UiHandler("addWeaponLink")
	public void addWeaponLink(ClickEvent e) {
		presenter.addWeapon(weaponSelection.getValue(weaponSelection
				.getSelectedIndex()));
	}

	@UiHandler("addArmourLink")
	public void addArmourLink(ClickEvent e) {
		presenter.addArmour(armourSelection.getValue(armourSelection
				.getSelectedIndex()));
	}

	@UiHandler("addArtifactLink")
	public void addArtifactLink(ClickEvent e) {
		presenter.addArtifact(artifactSelection.getValue(artifactSelection
				.getSelectedIndex()));
	}

	@UiHandler("addGeneralSkillLink")
	public void addGeneralSkillLink(ClickEvent e) {
		presenter.addGeneralSkill(generalSkillSelection
				.getValue(generalSkillSelection.getSelectedIndex()));
		generalSkillDataProvider.refresh();
		generalSkillDataProvider.flush();
	}

	@UiHandler("addSkillLink")
	public void addSkillLink(ClickEvent e) {
		presenter.addSkill(skillSelection.getValue(skillSelection
				.getSelectedIndex()));
		skillDataProvider.refresh();
		skillDataProvider.flush();
	}

	@UiHandler("replaceArmour")
	public void replaceArmour(ClickEvent e) {
		presenter.replaceArmour(Integer.parseInt(replacementArmourField.getValue(replacementArmourField.getSelectedIndex())));
	}

	@Override
	public HasText getWyrdField() {
		return wyrdField;
	}
	
	private HasText handedValue = new HasText() {
		@Override
		public void setText(String text) {
			for (int i = 0; i < handedField.getItemCount(); i++) {
				if (handedField.getValue(i).equals(text)) {
					handedField.setSelectedIndex(i);
					return;
				}
			}
			handedField.setSelectedIndex(0);
		}

		@Override
		public String getText() {
			return handedField.getValue(handedField.getSelectedIndex());
		}
	};

	@Override
	public HasText getHandedField() {
		return handedValue;
	}

	private HasText skillRanksValue = new HasText() {
		@Override
		public void setText(String text) {
			for (int i = 0; i < skillRanksField.getItemCount(); i++) {
				if (skillRanksField.getValue(i).equals(text)) {
					skillRanksField.setSelectedIndex(i);
					return;
				}
			}
			skillRanksField.setSelectedIndex(0);
		}

		@Override
		public String getText() {
			return skillRanksField.getValue(skillRanksField.getSelectedIndex());
		}
	};

	@Override
	public HasText getSkillRanksField() {
		return skillRanksValue;
	}

	@Override
	public HasText getCharacterCostField() {
		return characterCostField;
	}

	@Override
	public void setGeneralSkillList(List<CharacterGeneralSkillResult> skills) {
		generalSkillDataProvider.setList(skills);
		generalSkillDataProvider.refresh();
	}

	@Override
	public void setArmourSelectionList(List<GameArmourResult> armour) {
		armourSelection.clear();
		for (GameArmourResult a : armour) {
			armourSelection.addItem(a.getName());
		}
	}

	@Override
	public void setArtifactSelectionList(List<GameArtifactResult> artifacts) {
		artifactSelection.clear();
		for (GameArtifactResult a : artifacts) {
			artifactSelection.addItem(a.getName());
		}
	}

	@Override
	public void setWeaponSelectionList(List<GameWeaponResult> weapons) {
		weaponSelection.clear();
		for (GameWeaponResult w : weapons) {
			weaponSelection.addItem(w.getName());
		}
	}

	@Override
	public void setGeneralSkillSelectionList(List<String> generalSkills) {
		generalSkillSelection.clear();
		for (String s : generalSkills) {
			generalSkillSelection.addItem(s);
		}
	}

	@Override
	public void setSkillSelectionList(List<String> skills) {
		skillSelection.clear();
		for (String s : skills) {
			skillSelection.addItem(s);
		}
	}

	@Override
	public void setWeaponList(List<GameWeaponResult> weapons) {
		List<GameWeaponResult> sl = weaponDataProvider.getList();
		sl.clear();
		sl.addAll(weapons);
		weaponList.setVisibleRange(0, sl.size());
		weaponDataProvider.refresh();
	}

	@Override
	public void setArmourList(List<GameArmourResult> armour, List<Integer> indexes) {
		List<GameArmourResult> sl = armourDataProvider.getList();
		sl.clear();
		sl.addAll(armour);
		armourList.setVisibleRange(0, sl.size());
		armourDataProvider.refresh();
		replacementArmourField.clear();
		for (int i = 0; i < armourDataProvider.getList().size(); i++) {
			GameArmourResult a = armourDataProvider.getList().get(i);
			replacementArmourField.addItem(a.getName(), Integer.toString(indexes.get(i)));
		}
	}

	@Override
	public void setArtifactList(List<GameArtifactResult> artifact) {
		List<GameArtifactResult> sl = artifactDataProvider.getList();
		sl.clear();
		sl.addAll(artifact);
		artifactList.setVisibleRange(0, sl.size());
		artifactDataProvider.refresh();
	}

	@Override
	public List<CharacterGeneralSkillResult> getSkillsList() {
		return generalSkillDataProvider.getList();
	}

	@Override
	public void setGameMasterControls(boolean gm) {
		addArmourCommands.setVisible(gm);
		addWeaponCommands.setVisible(gm);
		addArtifactCommands.setVisible(gm);
		addGeneralSkillCommands.setVisible(gm);
		addSkillCommands.setVisible(gm);
	}
	
	@UiHandler ("uploadPortraitLink")
	public void uploadPortraitLink(ClickEvent e) {
		presenter.uploadPortrait();
	}

	public class SkillTreeModel implements TreeViewModel {

		@Override
		public <T> NodeInfo<?> getNodeInfo(T value) {

			if (value == null) {

				List<HasCell<CharacterGeneralSkillResult, ?>> hasCells = new ArrayList<HasCell<CharacterGeneralSkillResult, ?>>();

				Column<CharacterGeneralSkillResult, String> nameColumn = new Column<CharacterGeneralSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterGeneralSkillResult w) {
						return w.getGameGeneralSkill().getName() + " ";
					}
				};
				hasCells.add(nameColumn);

				IdentityColumn<CharacterGeneralSkillResult> removeColumn = new IdentityColumn<CharacterGeneralSkillResult>(new ImageButtonCell<CharacterGeneralSkillResult>("Remove", images.deleteobj()) {
					@Override
					public void execute(CharacterGeneralSkillResult value) {
						presenter.selectGeneralSkill(value);
						presenter.removeGeneralSkill();
						generalSkillDataProvider.refresh();
						generalSkillDataProvider.flush();
					}
				});
				hasCells.add(removeColumn);

				final Column<CharacterGeneralSkillResult, String> levelColumn = new Column<CharacterGeneralSkillResult, String>(new EditTextCell()) {
					@Override
					public String getValue(CharacterGeneralSkillResult w) {
						return w.getLevel() == null ? "0" : w.getLevel().toString();
					}
				};
				levelColumn.setCellStyleNames(resources.style().editableCell());
				levelColumn.setFieldUpdater(new FieldUpdater<CharacterGeneralSkillResult, String>() {
					@Override
					public void update(int index, CharacterGeneralSkillResult w, String text) {
						if (text == null || "".equals(text)) {
							w.setLevel(0);
						}
						try {
							w.setLevel(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							w.setLevel(0);
						}
						presenter.validate();
					}
				});
				hasCells.add(levelColumn);
				final Column<CharacterGeneralSkillResult, String> slashColumn = new Column<CharacterGeneralSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterGeneralSkillResult w) {
						return "/";
					}
				};
				hasCells.add(slashColumn);

				final Column<CharacterGeneralSkillResult, String> modifierColumn = new Column<CharacterGeneralSkillResult, String>(new EditTextCell()) {
					@Override
					public String getValue(CharacterGeneralSkillResult w) {
						return w.getModifier() == null ? "00" : zz.format(w.getModifier());
					}
				};
				modifierColumn.setCellStyleNames(resources.style().editableCell());
				modifierColumn.setFieldUpdater(new FieldUpdater<CharacterGeneralSkillResult, String>() {
					@Override
					public void update(int index, CharacterGeneralSkillResult w, String text) {
						if (text == null || "".equals(text)) {
							w.setLevel(0);w.setModifier(0);w.setRanks(0);
						}
						try {
							w.setModifier(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							w.setModifier(0);
						}
						presenter.validate();
					}
				});
				hasCells.add(modifierColumn);

				final Column<CharacterGeneralSkillResult, String> openBraceColumn = new Column<CharacterGeneralSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterGeneralSkillResult w) {
						return " (";
					}
				};
				hasCells.add(openBraceColumn);

				final Column<CharacterGeneralSkillResult, String> ranksColumn = new Column<CharacterGeneralSkillResult, String>(new EditTextCell()) {
					@Override
					public String getValue(CharacterGeneralSkillResult w) {
						return w.getRanks() == null ? "0" : w.getRanks().toString();
					}
				};
				ranksColumn.setCellStyleNames(resources.style().editableCell());
				ranksColumn.setFieldUpdater(new FieldUpdater<CharacterGeneralSkillResult, String>() {
					@Override
					public void update(int index, CharacterGeneralSkillResult w, String text) {
						if (text == null || "".equals(text)) {
							w.setRanks(0);
						}
						try {
							int ranks = Integer.parseInt(text);
							if (ranks < 0) {
								ranks = 0;
							} else if (ranks > 2) {
								ranks = 2;
							}
							w.setRanks(ranks);
							w.setLevel(SKILL_LEVEL_BY_RANK_COST[ranks][findAttribute("V")]);
							w.setModifier(SKILL_MODIFIER_BY_RANK_COST[ranks][findAttribute("V")]);
						} catch (NumberFormatException e) {
							w.setRanks(0);
						}
						generalSkillDataProvider.refresh();
						generalSkillDataProvider.flush();
						presenter.validate();
					}
				});
				hasCells.add(ranksColumn);
				final Column<CharacterGeneralSkillResult, String> closeBraceColumn = new Column<CharacterGeneralSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterGeneralSkillResult w) {
						return ")";
					}
				};
				hasCells.add(closeBraceColumn);

				CompositeCell<CharacterGeneralSkillResult> cl = new CompositeCell<CharacterGeneralSkillResult>(hasCells);

				return new DefaultNodeInfo<CharacterGeneralSkillResult>(generalSkillDataProvider, cl, generalSkillSelectionModel, null);

			} else if (value instanceof CharacterGeneralSkillResult) {
				
				List<HasCell<CharacterSkillResult, ?>> hasCells = new ArrayList<HasCell<CharacterSkillResult, ?>>();

				Column<CharacterSkillResult, String> nameColumn = new Column<CharacterSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterSkillResult w) {
						return w.getGameSkill().getName() + " ";
					}
				};
				hasCells.add(nameColumn);

				IdentityColumn<CharacterSkillResult> removeColumn = new IdentityColumn<CharacterSkillResult>(new ImageButtonCell<CharacterSkillResult>("Remove", images.deleteobj()) {
					@Override
					public void execute(CharacterSkillResult value) {
						skillDataProvider.refresh();
						skillDataProvider.flush();
						presenter.selectSkill(value);
						presenter.removeSkill();
						skillDataProvider.refresh();
						skillDataProvider.flush();
					}
				});
				hasCells.add(removeColumn);

				final Column<CharacterSkillResult, String> levelColumn = new Column<CharacterSkillResult, String>(new EditTextCell()) {
					@Override
					public String getValue(CharacterSkillResult w) {
						return w.getLevel() == null ? "0" : w.getLevel().toString();
					}
				};
				levelColumn.setCellStyleNames(resources.style().editableCell());
				levelColumn.setFieldUpdater(new FieldUpdater<CharacterSkillResult, String>() {
					@Override
					public void update(int index, CharacterSkillResult w, String text) {
						if (text == null || "".equals(text)) {
							w.setLevel(0);
						}
						try {
							w.setLevel(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							w.setLevel(0);
						}
						presenter.validate();
					}
				});
				hasCells.add(levelColumn);
				final Column<CharacterSkillResult, String> slashColumn = new Column<CharacterSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterSkillResult w) {
						return "/";
					}
				};
				hasCells.add(slashColumn);
				final Column<CharacterSkillResult, String> modifierColumn = new Column<CharacterSkillResult, String>(new EditTextCell()) {
					@Override
					public String getValue(CharacterSkillResult w) {
						return w.getModifier() == null ? "00" : zz.format(w.getModifier());
					}
				};
				modifierColumn.setCellStyleNames(resources.style().editableCell());
				modifierColumn.setFieldUpdater(new FieldUpdater<CharacterSkillResult, String>() {
					@Override
					public void update(int index, CharacterSkillResult w, String text) {
						if (text == null || "".equals(text)) {
							w.setModifier(0);
						}
						try {
							w.setModifier(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							w.setModifier(0);
						}
						presenter.validate();
					}
				});
				hasCells.add(modifierColumn);
				final Column<CharacterSkillResult, String> openBraceColumn = new Column<CharacterSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterSkillResult w) {
						return " (";
					}
				};
				hasCells.add(openBraceColumn);
				final Column<CharacterSkillResult, String> ranksColumn = new Column<CharacterSkillResult, String>(new EditTextCell()) {
					@Override
					public String getValue(CharacterSkillResult w) {
						return w.getRanks() == null ? "0" : w.getRanks().toString();
					}
				};
				ranksColumn.setCellStyleNames(resources.style().editableCell());
				ranksColumn.setFieldUpdater(new FieldUpdater<CharacterSkillResult, String>() {
					@Override
					public void update(int index, CharacterSkillResult w, String text) {
						if (text == null || "".equals(text)) {
							w.setRanks(0);
						}
						try {
							int ranks = Integer.parseInt(text);
							if (ranks < 0) {
								ranks = 0;
							} else if (ranks > 4) {
								ranks = 4;
							}
							w.setRanks(ranks);
							int generalRanks = w.getGeneralSkill().getRanks() == null ? 0 : w.getGeneralSkill().getRanks();
							int totalRanks = ranks + generalRanks;
							w.setLevel(SKILL_LEVEL_BY_RANK_COST[totalRanks][findAttribute(w.getGameSkill().getAttribute())]);
							w.setModifier(SKILL_MODIFIER_BY_RANK_COST[totalRanks][findAttribute(w.getGameSkill().getAttribute())]);
						} catch (NumberFormatException e) {
							w.setRanks(0);
						}
						presenter.validate();
						skillDataProvider.refresh();
						skillDataProvider.flush();
					}
				});
				hasCells.add(ranksColumn);
				final Column<CharacterSkillResult, String> closeBraceColumn = new Column<CharacterSkillResult, String>(new TextCell()) {
					@Override
					public String getValue(CharacterSkillResult w) {
						return ")";
					}
				};
				hasCells.add(closeBraceColumn);

				CompositeCell<CharacterSkillResult> cl = new CompositeCell<CharacterSkillResult>(hasCells);

				CharacterGeneralSkillResult gsr = (CharacterGeneralSkillResult) value;
				skillDataProvider = new ListDataProvider<CharacterSkillResult>(gsr.getSkills());
			
				return new DefaultNodeInfo<CharacterSkillResult>(skillDataProvider, cl, skillSelectionModel, null);

			} else {
				return null;
			}
		}

		@Override
		public boolean isLeaf(Object value) {
			if (value instanceof CharacterSkillResult) {
				return true;
			}
			return false;
		}

		private int findAttribute(String ats) {

			int i = 0;
			if ("M".equals(ats)) {
				i = Integer.parseInt(mettleField.getText());
			} else if ("G".equals(ats)) {
				i = Integer.parseInt(glamourField.getText());
			} else if ("W".equals(ats)) {
				i = Integer.parseInt(witField.getText());
			} else if ("S".equals(ats)) {
				i = Integer.parseInt(spiritField.getText());
			} else {
				i = Integer.parseInt(vigourField.getText());
			}
			return i - 1;

		}

	}

}
