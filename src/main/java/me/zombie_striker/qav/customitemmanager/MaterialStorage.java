package me.zombie_striker.qav.customitemmanager;

import me.zombie_striker.qav.util.HeadUtil;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MaterialStorage {

	private static final MaterialStorage EMPTY = new MaterialStorage(null, 0, 0);

	private static final List<MaterialStorage> store = new ArrayList<>();
	private final int d;
	private final Material m;
	private final int variant;
	private final String specialValues;
	private final String specialValues2;

	private MaterialStorage(Material m, int d, int var) {
		this(m, d, var, null);
	}

	private MaterialStorage(Material m, int d, int var, String extraData) {
		this(m, d, var, extraData, null);
	}

	private MaterialStorage(Material m, int d, int var, String extraData, String ed2) {
		this.m = m;
		this.d = d;
		this.variant = var;
		this.specialValues = extraData;
		this.specialValues2 = ed2;
	}

	public static MaterialStorage getMS(Material m, int d, int var) {
		return getMS(m, d, var, null);
	}

	public static MaterialStorage getMS(Material m, int d, int var, String extraValue) {
		return getMS(m, d, var, extraValue, null);
	}

	public static MaterialStorage getMS(Material m, int d, int var, String extraValue, String ev2) {
		for (MaterialStorage k : store) {
			if (matchesMaterials(k, m, d)) if (matchVariants(k, var)) if (matchHeads(k, extraValue, ev2))
				return k;
		}
		MaterialStorage mm = new MaterialStorage(m, d, var, extraValue, ev2);
		store.add(mm);
		return mm;
	}

	private static boolean matchesMaterials(MaterialStorage k, Material m, int d) {
		return (k.m == m && (k.d == d || k.d == -1));
	}

	public static boolean matchVariants(MaterialStorage k, int var) {
		return (!k.hasVariant() && var == 0) || (k.variant == var);
	}

	public static boolean matchHeads(MaterialStorage k, String ex1, String ex2) {
		boolean exb1 = (!k.hasSpecialValue() || k.hasSpecialValue2()
				|| (ex1 != null && (ex1.equals("-1") || k.getSpecialValue().equals(ex1))));
		boolean exb2 = (!k.hasSpecialValue2()
				|| (ex2 != null && (ex2.equals("-1") || k.getSpecialValue2().equals(ex2))));
		return exb1 && exb2;
	}

	public static MaterialStorage getMS(ItemStack is) {
		return getMS(is, getVariant(is));
	}

	@SuppressWarnings("deprecation")
	public static MaterialStorage getMS(ItemStack is, int variant) {

		if (is == null) {
			return EMPTY;
		}

		String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner()
				: null;
		String temp = null;
		if (extraData != null) {
			temp = HeadUtil.getTexture(is);
		}
		try {
			return getMS(is.getType(), is.getItemMeta().hasCustomModelData() ? is.getItemMeta().getCustomModelData() : 0, variant,
					is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner() : null, temp);

		} catch (Error | Exception ignored) {
		}
		return getMS(is.getType(), is.getDurability(), variant,
				is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner() : null, temp);
	}

	@SuppressWarnings("deprecation")
	public static int getVariant(ItemStack is) {
		if (is != null)
			if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
				for (String lore : Objects.requireNonNull(is.getItemMeta().getLore())) {
					if (lore.startsWith(QAMain.S_ITEM_VARIANTS_NEW)) {
						try {
							return Integer.parseInt(lore.split(QAMain.S_ITEM_VARIANTS_NEW)[1].trim());
						} catch (Error | Exception e4) {
							e4.printStackTrace();
							return 0;
						}
					} else if (lore.startsWith(QAMain.S_ITEM_VARIANTS_LEGACY)) {
						try {
							return Integer.parseInt(lore.split(QAMain.S_ITEM_VARIANTS_LEGACY)[1].trim());
						} catch (Error | Exception e4) {
							e4.printStackTrace();
							return 0;
						}
					}
				}
			}
		return 0;
	}

	public int getData() {
		return d;
	}

	public boolean hasSpecialValue() {
		return specialValues != null;
	}

	public String getSpecialValue() {
		return specialValues;
	}

	public boolean hasSpecialValue2() {
		return specialValues2 != null;
	}

	public String getSpecialValue2() {
		return specialValues2;
	}

	public Material getMat() {
		return m;
	}

	public boolean hasVariant() {
		return variant > 0;
	}
}
