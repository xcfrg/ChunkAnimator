package lumien.chunkanimator.config;

import java.util.ArrayList;
import java.util.List;

import lumien.chunkanimator.ChunkAnimator;
import lumien.chunkanimator.lib.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ChunkAnimatorConfigGui extends GuiConfig
{
	public ChunkAnimatorConfigGui(GuiScreen parent)
	{
		super(parent, getConfigElements(), Reference.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ChunkAnimator.INSTANCE.config.getString()));
	}

	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.addAll(ChunkAnimator.INSTANCE.config.getConfigElements());
		return list;
	}
}
