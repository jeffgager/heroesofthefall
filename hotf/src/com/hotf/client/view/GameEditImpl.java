package com.hotf.client.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameGeneralSkillResult;
import com.hotf.client.action.result.GameImplementResult;
import com.hotf.client.action.result.GameSkillResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.view.control.HotfCellBrowser;
import com.hotf.client.view.control.RichTextEditor;

public class GameEditImpl extends Composite implements GameEdit {

	private static GameViewImplUiBinder uiBinder = GWT.create(GameViewImplUiBinder.class);

	interface GameViewImplUiBinder extends UiBinder<Widget, GameEditImpl> {
	}

	private Presenter presenter;
	
	private ListDataProvider<GameWeaponResult> weaponDataProvider;
	private ListDataProvider<GameArmourResult> armourDataProvider;
	private ListDataProvider<GameArtifactResult> artifactDataProvider;
	private ListDataProvider<GameGeneralSkillResult> generalSkillModel;
	private ListDataProvider<GameSkillResult> skillModel;
	private ListDataProvider<GameImplementResult> weaponModel;
	private ListDataProvider<GameArtifactResult> artifactModel;

	private SingleSelectionModel<GameWeaponResult> weaponSelection;
	private SingleSelectionModel<GameArmourResult> armourSelection;
	private SingleSelectionModel<GameArtifactResult> artifactSelection;
	private SingleSelectionModel<GameSkillResult> skillSelection;
	private SingleSelectionModel<GameImplementResult> skillWeaponSelection;
	private SingleSelectionModel<GameArtifactResult> skillArtifactSelection;

	@UiField(provided = true) final Resources resources;
	@UiField(provided = true) RichTextEditor descriptionField;
	@UiField(provided = true) DataGrid<GameWeaponResult> weaponsList;
	@UiField(provided = true) DataGrid<GameArmourResult> armourList;
	@UiField(provided = true) DataGrid<GameArtifactResult> artifactList;
	@UiField(provided = true) HotfCellBrowser skillList;
	@UiField TextBox titleField;
	@UiField ListBox weaponChooser;
	@UiField ListBox artifactChooser;
	@UiField TabLayoutPanel tabPanel;

	public GameEditImpl(ClientFactory clientFactory) {

		super();
	
		this.resources = clientFactory.getResources();
		this.descriptionField = clientFactory.createRichTextEditor();

		generalSkillModel = new ListDataProvider<GameGeneralSkillResult>(new ArrayList<GameGeneralSkillResult>());
		skillList = new HotfCellBrowser(new SkillTreeModel(), null);
		skillList.setWidth("100%");
		skillList.setHeight("390px");
		skillList.setDefaultColumnWidth(320);
		skillList.setMinimumColumnWidth(320);
		skillSelection = new SingleSelectionModel<GameSkillResult>();
		SplitLayoutPanel splitPanel = skillList.getWidget();
		splitPanel.setWidgetSize(splitPanel.getWidget(0), 320.0D);

		weaponDataProvider = new ListDataProvider<GameWeaponResult>();
		weaponsList = new DataGrid<GameWeaponResult>();
		weaponsList.setWidth("99%");
		weaponsList.setHeight("390px");
		weaponDataProvider.addDataDisplay(weaponsList);
		weaponSelection = new SingleSelectionModel<GameWeaponResult>();
		weaponsList.setSelectionModel(weaponSelection);

		armourDataProvider = new ListDataProvider<GameArmourResult>();
		armourList = new DataGrid<GameArmourResult>();
		armourList.setWidth("99%");
		armourList.setHeight("390px");
		armourDataProvider.addDataDisplay(armourList);
		armourSelection = new SingleSelectionModel<GameArmourResult>();
		armourList.setSelectionModel(armourSelection);

		artifactDataProvider = new ListDataProvider<GameArtifactResult>();
		artifactList = new DataGrid<GameArtifactResult>();
		artifactList.setWidth("99%");
		artifactList.setHeight("390px");
		artifactDataProvider.addDataDisplay(artifactList);
		artifactSelection = new SingleSelectionModel<GameArtifactResult>();
		artifactList.setSelectionModel(artifactSelection);

		skillWeaponSelection = new SingleSelectionModel<GameImplementResult>();

		Column<GameWeaponResult, String> nameColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				return w.getName();
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				w.setName(v);
				List<GameWeaponResult> sl = weaponDataProvider.getList();
				weaponChooser.clear();
				for (GameWeaponResult w1 : sl) {
					weaponChooser.addItem(w1.getName());
				}
			}
		});
		weaponsList.addColumn(nameColumn, "Name");

		Column<GameWeaponResult, String> minRangeColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getMinRange();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		minRangeColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setMinRange(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setMinRange(null);
				}
			}
		});
		weaponsList.addColumn(minRangeColumn, "Min Range");
		weaponsList.setColumnWidth(minRangeColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> maxRangeColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getMaxRange();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		maxRangeColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setMaxRange(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setMaxRange(null);
				}
			}
		});
		weaponsList.addColumn(maxRangeColumn, "Max Range");
		weaponsList.setColumnWidth(maxRangeColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> defenceColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getDefence();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		defenceColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setDefence(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setDefence(null);
				}
			}
		});
		weaponsList.addColumn(defenceColumn, "Defence");
		weaponsList.setColumnWidth(defenceColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> slashDamageColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getSlashDamage();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		slashDamageColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setSlashDamage(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setSlashDamage(null);
				}
			}
		});
		weaponsList.addColumn(slashDamageColumn, "Slash Damage");
		weaponsList.setColumnWidth(slashDamageColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> crushDamageColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getCrushDamage();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		crushDamageColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setCrushDamage(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setCrushDamage(null);
				}
			}
		});
		weaponsList.addColumn(crushDamageColumn, "Crush Damage");
		weaponsList.setColumnWidth(crushDamageColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> pierceDamageColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getPierceDamage();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		pierceDamageColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setPierceDamage(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setPierceDamage(null);
				}
			}
		});
		weaponsList.addColumn(pierceDamageColumn, "Pierce Damage");
		weaponsList.setColumnWidth(pierceDamageColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> damageRatingColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getDamageRating();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		damageRatingColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setDamageRating(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setDamageRating(null);
				}
			}
		});
		weaponsList.addColumn(damageRatingColumn, "Damage Rating");
		weaponsList.setColumnWidth(damageRatingColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> strengthRatingColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getStrengthRating();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		strengthRatingColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setStrengthRating(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setStrengthRating(null);
				}
			}
		});
		weaponsList.addColumn(strengthRatingColumn, "Strength Rating");
		weaponsList.setColumnWidth(strengthRatingColumn, 72, Unit.PX);

		Column<GameWeaponResult, String> initiativeColumn = new Column<GameWeaponResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameWeaponResult w) {
				Integer v = w.getInitiative();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		initiativeColumn.setFieldUpdater(new FieldUpdater<GameWeaponResult, String>() {
			@Override
			public void update(int index, GameWeaponResult w, String v) {
				try {
					w.setInitiative(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setInitiative(null);
				}
			}
		});
		weaponsList.addColumn(initiativeColumn, "Initiative");
		weaponsList.setColumnWidth(initiativeColumn, 72, Unit.PX);

		Column<GameArmourResult, String> armourNameColumn = new Column<GameArmourResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameArmourResult w) {
				return w.getName();
			}
		};
		armourNameColumn.setFieldUpdater(new FieldUpdater<GameArmourResult, String>() {
			@Override
			public void update(int index, GameArmourResult w, String v) {
				w.setName(v);
			}
		});
		armourList.addColumn(armourNameColumn, "Name");

		Column<GameArmourResult, String> slashDefenceColumn = new Column<GameArmourResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameArmourResult w) {
				Integer v = w.getSlashDefence();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		slashDefenceColumn.setFieldUpdater(new FieldUpdater<GameArmourResult, String>() {
			@Override
			public void update(int index, GameArmourResult w, String v) {
				try {
					w.setSlashDefence(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setSlashDefence(null);
				}
			}
		});
		armourList.addColumn(slashDefenceColumn, "Slash Defence");
		armourList.setColumnWidth(slashDefenceColumn, 72, Unit.PX);

		Column<GameArmourResult, String> crushDefenceColumn = new Column<GameArmourResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameArmourResult w) {
				Integer v = w.getCrushDefence();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		crushDefenceColumn.setFieldUpdater(new FieldUpdater<GameArmourResult, String>() {
			@Override
			public void update(int index, GameArmourResult w, String v) {
				try {
					w.setCrushDefence(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setCrushDefence(null);
				}
			}
		});
		armourList.addColumn(crushDefenceColumn, "Crush Defence");
		armourList.setColumnWidth(crushDefenceColumn, 72, Unit.PX);

		Column<GameArmourResult, String> pierceDefenceColumn = new Column<GameArmourResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameArmourResult w) {
				Integer v = w.getPierceDefence();
				return v == null ? "-" : Integer.toString(v);
			}
		};
		pierceDefenceColumn.setFieldUpdater(new FieldUpdater<GameArmourResult, String>() {
			@Override
			public void update(int index, GameArmourResult w, String v) {
				try {
					w.setPierceDefence(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setPierceDefence(null);
				}
			}
		});
		armourList.addColumn(pierceDefenceColumn, "Pierce Defence");
		armourList.setColumnWidth(pierceDefenceColumn, 72, Unit.PX);

		Column<GameArmourResult, String> armourInitiativeColumn = new Column<GameArmourResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameArmourResult w) {
				Integer v = w.getInitiative();
				return v == null ? "0" : Integer.toString(v);
			}
		};
		armourInitiativeColumn.setFieldUpdater(new FieldUpdater<GameArmourResult, String>() {
			@Override
			public void update(int index, GameArmourResult w, String v) {
				try {
					w.setInitiative(Integer.valueOf(v));
				} catch (NumberFormatException e) {
					w.setInitiative(null);
				}
			}
		});
		armourList.addColumn(armourInitiativeColumn, "Initiative");
		armourList.setColumnWidth(armourInitiativeColumn, 72, Unit.PX);

		armourList.setColumnWidth(armourNameColumn, 100, Unit.PCT);

		Column<GameArtifactResult, String> artifactNameColumn = new Column<GameArtifactResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameArtifactResult w) {
				return w.getName();
			}
		};
		artifactNameColumn.setFieldUpdater(new FieldUpdater<GameArtifactResult, String>() {
			@Override
			public void update(int index, GameArtifactResult w, String v) {
				w.setName(v);
			}
		});
		artifactList.addColumn(artifactNameColumn, "Name");

		Column<GameArtifactResult, String> artifactEffectColumn = new Column<GameArtifactResult, String>(new TextInputCell()) {
			@Override
			public String getValue(GameArtifactResult w) {
				Integer v = w.getEffect();
				return v == null ? "0" : Integer.toString(v);
			}
		};
		artifactEffectColumn.setFieldUpdater(new FieldUpdater<GameArtifactResult, String>() {
			@Override
			public void update(int index, GameArtifactResult w, String v) {
				try {
					w.setEffect(Integer.valueOf(v));
					artifactChooser.clear();
					List<GameArtifactResult> sl = artifactDataProvider.getList();
					for (GameArtifactResult w1 : sl) {
						artifactChooser.addItem(w1.getName());
					}
				} catch (NumberFormatException e) {
					w.setEffect(null);
				}
			}
		});
		artifactList.addColumn(artifactEffectColumn, "Effect");
		artifactList.setColumnWidth(artifactEffectColumn, 72, Unit.PX);

		artifactList.setColumnWidth(artifactNameColumn, 100, Unit.PCT);

		initWidget(uiBinder.createAndBindUi(this));

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setGeneralSkills(List<GameGeneralSkillResult> skills) {
		generalSkillModel.setList(skills);
		generalSkillModel.refresh();
	}

	@Override
	public void setArmourList(List<GameArmourResult> armour) {
		List<GameArmourResult> sl = armourDataProvider.getList();
		sl.clear();
		sl.addAll(armour);
		armourList.setRowCount(sl.size(), true);
		armourList.setVisibleRange(0, sl.size());
		armourList.setPageStart(0);
		armourList.setPageSize(sl.size());
	}

	@Override
	public void setArtifactsList(List<GameArtifactResult> artifacts) {
		List<GameArtifactResult> sl = artifactDataProvider.getList();
		sl.clear();
		sl.addAll(artifacts);
		artifactList.setRowCount(sl.size(), true);
		artifactList.setVisibleRange(0, sl.size());
		artifactList.setPageStart(0);
		artifactList.setPageSize(sl.size());
		artifactChooser.clear();
		for (GameArtifactResult w : sl) {
			artifactChooser.addItem(w.getName());
		}
	}

	@Override
	public void setWeaponList(List<GameWeaponResult> weapons) {
		List<GameWeaponResult> sl = weaponDataProvider.getList();
		sl.clear();
		sl.addAll(weapons);
		weaponsList.setRowCount(sl.size(), true);
		weaponsList.setVisibleRange(0, sl.size());
		weaponsList.setPageStart(0);
		weaponsList.setPageSize(sl.size());
		weaponChooser.clear();
		for (GameWeaponResult w : sl) {
			weaponChooser.addItem(w.getName());
		}
	}

	@Override
	public HasText getTitleValue() {
		return titleField;
	}

	@Override
	public Focusable getTitleFocus() {
		return titleField;
	}

	@Override
	public HasHTML getDescriptionValue() {
		return descriptionField;
	}

	@Override
	public void setShowTabs(boolean value) {
		tabPanel.setVisible(value);
	}

	@UiHandler ("closeLink")
	public void close(ClickEvent e) {
		presenter.close();
	}
		
	@UiHandler ("saveLink")
	public void save(ClickEvent e) {
		presenter.save();
	}
	
	@UiHandler ("addWeaponButton")
	public void addWeaponButton(ClickEvent e) {
		presenter.addWeapon();
	}
	
	@UiHandler ("addArmourButton")
	public void addArmourButton(ClickEvent e) {
		presenter.addArmour();
	}

	@UiHandler ("addArtifactButton")
	public void addArtifactButton(ClickEvent e) {
		presenter.addArtifact();
	}

	@UiHandler ("addSkillWeaponButton")
	public void addSkillWeaponButton(ClickEvent e) {
		GameSkillResult skill = skillSelection.getSelectedObject();
		String weaponName = weaponChooser.getItemText(weaponChooser.getSelectedIndex());
		GameWeaponResult weapon = null;
		for (GameWeaponResult w : weaponDataProvider.getList()) {
			if (weaponName.equals(w.getName())) {
				weapon = w;
				break;
			}
		}
		if (skill != null && weapon != null) {
			List<GameImplementResult> l = weaponModel.getList();
			if (!l.contains(weapon)) {
				weaponModel.getList().add(weapon);
				List<String> skills = weapon.getSkillNames();
				if (!skills.contains(skill.getName())) {
					skills.add(skill.getName());
					presenter.addSkillWeapon(skill.getName(), weapon);
				}
			}
		}
	}
	
	@UiHandler ("removeSkillWeaponButton")
	public void removeSkillWeaponButton(ClickEvent e) {
		GameSkillResult skill = skillSelection.getSelectedObject();
		GameWeaponResult weapon = null;
		for (GameWeaponResult w : weaponDataProvider.getList()) {
			if (skillWeaponSelection.getSelectedObject().getName().equals(w.getName())) {
				weapon = w;
				break;
			}
		}
		if (skill != null && weapon != null) {
			weaponModel.getList().remove(weapon);
			List<String> skills = weapon.getSkillNames();
			skills.remove(skill.getName());
			presenter.removeSkillWeapon(skill.getName(), weapon);
		}
	}

	@UiHandler ("addSkillArtifactButton")
	public void addSkillArtifactButton(ClickEvent e) {
		GameSkillResult skill = skillSelection.getSelectedObject();
		String artifactName = artifactChooser.getItemText(artifactChooser.getSelectedIndex());
		GameArtifactResult artifact = null;
		for (GameArtifactResult w : artifactDataProvider.getList()) {
			if (artifactName.equals(w.getName())) {
				artifact = w;
				break;
			}
		}
		if (skill != null && artifact != null) {
			List<GameArtifactResult> l = artifactModel.getList();
			if (!l.contains(artifact)) {
				artifactModel.getList().add(artifact);
				List<String> skills = artifact.getSkillNames();
				if (!skills.contains(skill.getName())) {
					skills.add(skill.getName());
					presenter.addSkillArtifact(skill.getName(), artifact);
				}
			}
		}
	}
	
	@UiHandler ("removeSkillArtifactButton")
	public void removeSkillArtifactButton(ClickEvent e) {
		GameSkillResult skill = skillSelection.getSelectedObject();
		GameArtifactResult artifact = null;
		for (GameArtifactResult w : artifactDataProvider.getList()) {
			if (skillArtifactSelection.getSelectedObject().getName().equals(w.getName())) {
				artifact = w;
				break;
			}
		}
		if (skill != null && artifact != null) {
			artifactModel.getList().remove(artifact);
			List<String> skills = artifact.getSkillNames();
			skills.remove(skill.getName());
			presenter.removeSkillArtifact(skill.getName(), artifact);
		}
	}

	@UiHandler ("tabPanel")
	public void downArmourButton(SelectionEvent<Integer> event) {
		if (event.getSelectedItem() == 1) {
			armourList.onResize();
		} else if (event.getSelectedItem() == 2) {
			weaponsList.onResize();
		} else if (event.getSelectedItem() == 3) {
			artifactList.onResize();
		}
	}
	
	public class SkillTreeModel implements TreeViewModel {

		@Override
		public <T> NodeInfo<?> getNodeInfo(T value) {
			if (value == null) {
		        Cell<GameGeneralSkillResult> cell = new AbstractCell<GameGeneralSkillResult>() {
		            @Override
		            public void render(Context context, GameGeneralSkillResult value, SafeHtmlBuilder sb) {
		                if (value != null) {
		                    sb.appendEscaped(value.getName());
		                }
		            }
		        };
				return new DefaultNodeInfo<GameGeneralSkillResult>(generalSkillModel, cell);
			} else if (value instanceof GameGeneralSkillResult) {
		        Cell<GameSkillResult> cell = new AbstractCell<GameSkillResult>() {
		            @Override
		            public void render(Context context, GameSkillResult value, SafeHtmlBuilder sb) {
		                if (value != null) {
		                    sb.appendEscaped(value.getName() + " (" + value.getAttribute() + ")");
		                }
		            }
		        };
				GameGeneralSkillResult gsr = (GameGeneralSkillResult)value;
				skillModel = new ListDataProvider<GameSkillResult>(gsr.getSkills());
				return new DefaultNodeInfo<GameSkillResult>(skillModel, cell, skillSelection, null);
			} else if (value instanceof GameSkillResult) {
		        Cell<GameImplementResult> cell = new AbstractCell<GameImplementResult>() {
		            @Override
		            public void render(Context context, GameImplementResult value, SafeHtmlBuilder sb) {
		                if (value != null) {
		                    sb.appendEscaped(value.getName());
		                }
		            }
		        };
				GameSkillResult sr = (GameSkillResult)value;
				weaponModel = new ListDataProvider<GameImplementResult>(presenter.getSkillImplements(sr.getName()));
				return new DefaultNodeInfo<GameImplementResult>(weaponModel, cell, skillWeaponSelection, null);
			} else {
				return null;
			}
		}
		
		@Override
		public boolean isLeaf(Object value) {
			if (value instanceof GameWeaponResult) {
				return true;
			} else if (value instanceof GameSkillResult) {
				GameSkillResult skill = (GameSkillResult)value;
				if (presenter.getSkillImplements(skill.getName()).size() == 0) {
					return true;
				}
			}
			return false;
		}

	}

}
