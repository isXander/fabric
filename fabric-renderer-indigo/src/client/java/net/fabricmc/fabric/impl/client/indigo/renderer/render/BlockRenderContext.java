/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.client.indigo.renderer.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoLuminanceFix;

/**
 * Context for non-terrain block rendering.
 */
public class BlockRenderContext extends AbstractBlockRenderContext {
	private VertexConsumer vertexConsumer;

	@Override
	protected AoCalculator createAoCalc(BlockRenderInfo blockInfo) {
		return new AoCalculator(blockInfo) {
			@Override
			public int light(BlockPos pos, BlockState state) {
				return WorldRenderer.getLightmapCoordinates(blockInfo.blockView, state, pos);
			}

			@Override
			public float ao(BlockPos pos, BlockState state) {
				return AoLuminanceFix.INSTANCE.apply(blockInfo.blockView, pos, state);
			}
		};
	}

	@Override
	protected VertexConsumer getVertexConsumer(RenderLayer layer) {
		return vertexConsumer;
	}

	public void render(BlockRenderView blockView, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrixStack, VertexConsumer buffer, boolean cull, Random random, long seed, int overlay) {
		this.vertexConsumer = buffer;
		this.matrix = matrixStack.peek().getPositionMatrix();
		this.normalMatrix = matrixStack.peek().getNormalMatrix();
		this.overlay = overlay;

		blockInfo.random = random;
		blockInfo.seed = seed;
		blockInfo.recomputeSeed = false;

		aoCalc.clear();
		blockInfo.prepareForWorld(blockView, cull);
		blockInfo.prepareForBlock(state, pos, model.useAmbientOcclusion());

		model.emitBlockQuads(blockView, state, pos, blockInfo.randomSupplier, this);

		blockInfo.release();
		blockInfo.random = null;
		this.vertexConsumer = null;
	}
}
