package font;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class FontRenderer
{
	private FontShader shader;

	public FontRenderer()
	{
		shader = new FontShader();
	}

	public void render(ArrayList<TextGUI> texts)
	{
		prepare();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextManager.assembler.atlasID);
		for (TextGUI text : texts)
		{
			renderText(text);
		}

		endRendering();
	}

	public void cleanUp()
	{
		shader.cleanUp();
	}

	private void prepare()
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.start();
	}

	private void renderText(TextGUI text)
	{
		GL30.glBindVertexArray(text.data.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		shader.loadColor(text.color);
		shader.loadHighlight(text.highlight);
		shader.loadShadow(text.shadow);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.data.vertexCount);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	private void endRendering()
	{
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
