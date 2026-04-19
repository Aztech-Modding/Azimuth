package com.cake.azimuth.utility.client.model;

import com.simibubi.create.foundation.model.BakedQuadHelper;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Central utilities for copying, transforming, shifting, extracting, and combining baked quads.
 */
public class QuadTransformer {
    public static final long DEFAULT_RANDOM_SEED = 42L;
    private static final float UV_EPSILON = 1.0E-4f;
    private static final float NORMAL_EPSILON = 1.0E-6f;

    public static BakedQuad copy(final BakedQuad quad) {
        return BakedQuadHelper.clone(quad);
    }

    public static List<BakedQuad> copy(final List<BakedQuad> quads) {
        return map(quads, QuadTransformer::copy);
    }

    public static BakedQuad shiftSprite(final BakedQuad quad, final SpriteShiftEntry shift) {
        if (!matchesSpriteShift(quad, shift)) {
            return quad;
        }
        return shiftSpriteUnchecked(quad, shift);
    }

    public static BakedQuad shiftSpriteIfMatch(final BakedQuad quad, final @Nullable SpriteShiftEntry shift) {
        if (shift == null) {
            return quad;
        }
        return shiftSprite(quad, shift);
    }

    private static BakedQuad shiftSpriteUnchecked(final BakedQuad quad, final SpriteShiftEntry shift) {
        final int[] vertexData = Arrays.copyOf(quad.getVertices(), quad.getVertices().length);
        final int vertexCount = vertexData.length / BakedQuadHelper.VERTEX_STRIDE;
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            final float u = BakedQuadHelper.getU(vertexData, vertex);
            final float v = BakedQuadHelper.getV(vertexData, vertex);
            BakedQuadHelper.setU(vertexData, vertex, shift.getTargetU(u));
            BakedQuadHelper.setV(vertexData, vertex, shift.getTargetV(v));
        }
        return new BakedQuad(
                vertexData,
                quad.getTintIndex(),
                quad.getDirection(),
                shift.getTarget(),
                quad.isShade()
        );
    }

    public static List<BakedQuad> shiftSprites(final List<BakedQuad> quads, final SpriteShiftEntry shift) {
        return map(quads, quad -> shiftSprite(quad, shift));
    }

    @SafeVarargs
    public static List<BakedQuad> shiftSprites(final List<BakedQuad> quads,
                                               final @Nullable SpriteShiftEntry... shifts) {
        return shiftSprites(quads, quad -> findSpriteShift(quad, shifts));
    }

    public static List<BakedQuad> shiftSprites(final List<BakedQuad> quads,
                                               final Iterable<? extends SpriteShiftEntry> shifts) {
        return shiftSprites(quads, quad -> findSpriteShift(quad, shifts));
    }

    public static List<BakedQuad> shiftSprites(final List<BakedQuad> quads,
                                               final Function<BakedQuad, SpriteShiftEntry> shiftResolver) {
        return map(quads, quad -> shiftSpriteIfMatch(quad, shiftResolver.apply(quad)));
    }

    @SafeVarargs
    public static @Nullable SpriteShiftEntry findSpriteShift(final BakedQuad quad,
                                                             final @Nullable SpriteShiftEntry... shifts) {
        for (final SpriteShiftEntry shift : shifts) {
            if (shift != null && matchesSpriteShift(quad, shift)) {
                return shift;
            }
        }
        return null;
    }

    public static @Nullable SpriteShiftEntry findSpriteShift(final BakedQuad quad,
                                                             final Iterable<? extends SpriteShiftEntry> shifts) {
        for (final SpriteShiftEntry shift : shifts) {
            if (shift != null && matchesSpriteShift(quad, shift)) {
                return shift;
            }
        }
        return null;
    }

    public static boolean matchesSpriteShift(final BakedQuad quad, final SpriteShiftEntry shift) {
        return quad.getSprite() == shift.getOriginal() || uvWithinSprite(quad, shift.getOriginal());
    }

    public static boolean uvWithinSprite(final BakedQuad quad, final TextureAtlasSprite sprite) {
        final int[] vertexData = quad.getVertices();
        final int vertexCount = vertexData.length / BakedQuadHelper.VERTEX_STRIDE;
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            final float u = BakedQuadHelper.getU(vertexData, vertex);
            final float v = BakedQuadHelper.getV(vertexData, vertex);
            if (u < sprite.getU0() - UV_EPSILON || u > sprite.getU1() + UV_EPSILON
                    || v < sprite.getV0() - UV_EPSILON || v > sprite.getV1() + UV_EPSILON) {
                return false;
            }
        }
        return true;
    }

    public static BakedQuad transform(final BakedQuad quad, final Matrix4fc transform) {
        final int[] sourceVertices = quad.getVertices();
        final int[] transformedVertices = Arrays.copyOf(sourceVertices, sourceVertices.length);
        final Matrix3f normalTransform = new Matrix3f().set(transform).invert().transpose();
        final int vertexCount = transformedVertices.length / BakedQuadHelper.VERTEX_STRIDE;
        final Vector3f position = new Vector3f();
        final Vector3f normal = new Vector3f();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            final Vec3 sourcePosition = BakedQuadHelper.getXYZ(sourceVertices, vertex);
            transform.transformPosition(
                    (float) sourcePosition.x,
                    (float) sourcePosition.y,
                    (float) sourcePosition.z,
                    position
            );
            BakedQuadHelper.setXYZ(transformedVertices, vertex, new Vec3(position.x, position.y, position.z));

            final Vec3 sourceNormal = BakedQuadHelper.getNormalXYZ(sourceVertices, vertex);
            normal.set((float) sourceNormal.x, (float) sourceNormal.y, (float) sourceNormal.z);
            normalTransform.transform(normal);
            if (normal.lengthSquared() > NORMAL_EPSILON) {
                normal.normalize();
                BakedQuadHelper.setNormalXYZ(transformedVertices, vertex, new Vec3(normal.x, normal.y, normal.z));
            }
        }

        return new BakedQuad(
                transformedVertices,
                quad.getTintIndex(),
                faceAfterTransform(quad, transformedVertices, transform),
                quad.getSprite(),
                quad.isShade()
        );
    }

    public static List<BakedQuad> transform(final List<BakedQuad> quads, final Matrix4fc transform) {
        return map(quads, quad -> transform(quad, transform));
    }

    public static BakedQuad translate(final BakedQuad quad, final double x, final double y, final double z) {
        return transform(quad, new Matrix4f().translation((float) x, (float) y, (float) z));
    }

    public static List<BakedQuad> translate(final List<BakedQuad> quads,
                                            final double x,
                                            final double y,
                                            final double z) {
        return transform(quads, new Matrix4f().translation((float) x, (float) y, (float) z));
    }

    public static BakedQuad rotate(final BakedQuad quad, final Quaternionfc rotation) {
        return transform(quad, new Matrix4f().rotation(rotation));
    }

    public static BakedQuad rotate(final BakedQuad quad,
                                   final Quaternionfc rotation,
                                   final double pivotX,
                                   final double pivotY,
                                   final double pivotZ) {
        return transform(quad, aroundPivot(rotation, pivotX, pivotY, pivotZ));
    }

    public static List<BakedQuad> rotate(final List<BakedQuad> quads, final Quaternionfc rotation) {
        return transform(quads, new Matrix4f().rotation(rotation));
    }

    public static List<BakedQuad> rotate(final List<BakedQuad> quads,
                                         final Quaternionfc rotation,
                                         final double pivotX,
                                         final double pivotY,
                                         final double pivotZ) {
        return transform(quads, aroundPivot(rotation, pivotX, pivotY, pivotZ));
    }

    public static BakedModel requireModel(final PartialModel partialModel) {
        final BakedModel bakedModel = partialModel.get();
        if (bakedModel == null) {
            throw new IllegalStateException("Partial model has not been baked yet: " + partialModel.modelLocation());
        }
        return bakedModel;
    }

    public static BakedModel requireModel(final ResourceLocation modelLocation) {
        final ModelManager modelManager = Minecraft.getInstance().getModelManager();
        final BakedModel bakedModel = modelManager.getModel(ModelResourceLocation.standalone(modelLocation));
        if (bakedModel == modelManager.getMissingModel()) {
            throw new IllegalArgumentException("Missing baked model: " + modelLocation);
        }
        return bakedModel;
    }

    public static List<BakedQuad> extract(final BakedModel model) {
        return extract(model, null, ModelData.EMPTY, null);
    }

    public static List<BakedQuad> extract(final PartialModel partialModel) {
        return extract(requireModel(partialModel));
    }

    public static List<BakedQuad> extract(final ResourceLocation modelLocation) {
        return extract(requireModel(modelLocation));
    }

    public static List<BakedQuad> extract(final BakedModel model,
                                          final @Nullable BlockState state,
                                          final ModelData modelData,
                                          final @Nullable RenderType renderType) {
        return extract(model, state, RandomSource.create(), DEFAULT_RANDOM_SEED, modelData, renderType);
    }

    public static List<BakedQuad> extract(final PartialModel partialModel,
                                          final @Nullable BlockState state,
                                          final ModelData modelData,
                                          final @Nullable RenderType renderType) {
        return extract(requireModel(partialModel), state, modelData, renderType);
    }

    public static List<BakedQuad> extract(final ResourceLocation modelLocation,
                                          final @Nullable BlockState state,
                                          final ModelData modelData,
                                          final @Nullable RenderType renderType) {
        return extract(requireModel(modelLocation), state, modelData, renderType);
    }

    public static List<BakedQuad> extract(final BakedModel model,
                                          final @Nullable BlockState state,
                                          final RandomSource random,
                                          final long seed,
                                          final ModelData modelData,
                                          final @Nullable RenderType renderType) {
        final List<BakedQuad> quads = new ArrayList<>();
        for (final Direction face : Direction.values()) {
            random.setSeed(seed);
            quads.addAll(model.getQuads(state, face, random, modelData, renderType));
        }
        random.setSeed(seed);
        quads.addAll(model.getQuads(state, null, random, modelData, renderType));
        return quads;
    }

    public static List<BakedQuad> extract(final BakedModel model,
                                          final @Nullable BlockState state,
                                          final @Nullable Direction side,
                                          final RandomSource random,
                                          final ModelData modelData,
                                          final @Nullable RenderType renderType) {
        return new ArrayList<>(model.getQuads(state, side, random, modelData, renderType));
    }

    public static List<BakedQuad> extract(final PartialModel partialModel,
                                          final @Nullable BlockState state,
                                          final @Nullable Direction side,
                                          final RandomSource random,
                                          final ModelData modelData,
                                          final @Nullable RenderType renderType) {
        return extract(requireModel(partialModel), state, side, random, modelData, renderType);
    }

    public static List<BakedQuad> extract(final ResourceLocation modelLocation,
                                          final @Nullable BlockState state,
                                          final @Nullable Direction side,
                                          final RandomSource random,
                                          final ModelData modelData,
                                          final @Nullable RenderType renderType) {
        return extract(requireModel(modelLocation), state, side, random, modelData, renderType);
    }

    @SafeVarargs
    public static List<BakedQuad> combine(final List<BakedQuad>... quadGroups) {
        int size = 0;
        for (final List<BakedQuad> quadGroup : quadGroups) {
            size += quadGroup.size();
        }

        final List<BakedQuad> combined = new ArrayList<>(size);
        for (final List<BakedQuad> quadGroup : quadGroups) {
            combined.addAll(quadGroup);
        }
        return combined;
    }

    public static List<BakedQuad> combine(final List<BakedQuad> baseQuads, final PartialModel... partialModels) {
        final List<BakedQuad> combined = new ArrayList<>(baseQuads);
        for (final PartialModel partialModel : partialModels) {
            combined.addAll(extract(partialModel));
        }
        return combined;
    }

    public static List<BakedQuad> combine(final List<BakedQuad> baseQuads, final BakedModel... models) {
        final List<BakedQuad> combined = new ArrayList<>(baseQuads);
        for (final BakedModel model : models) {
            combined.addAll(extract(model));
        }
        return combined;
    }

    public static List<BakedQuad> combine(final List<BakedQuad> baseQuads, final ResourceLocation... modelLocations) {
        final List<BakedQuad> combined = new ArrayList<>(baseQuads);
        for (final ResourceLocation modelLocation : modelLocations) {
            combined.addAll(extract(modelLocation));
        }
        return combined;
    }

    public static List<BakedQuad> extractAll(final PartialModel... partialModels) {
        final List<BakedQuad> extracted = new ArrayList<>();
        for (final PartialModel partialModel : partialModels) {
            extracted.addAll(extract(partialModel));
        }
        return extracted;
    }

    public static List<BakedQuad> extractAll(final BakedModel... models) {
        final List<BakedQuad> extracted = new ArrayList<>();
        for (final BakedModel model : models) {
            extracted.addAll(extract(model));
        }
        return extracted;
    }

    public static List<BakedQuad> extractAll(final ResourceLocation... modelLocations) {
        final List<BakedQuad> extracted = new ArrayList<>();
        for (final ResourceLocation modelLocation : modelLocations) {
            extracted.addAll(extract(modelLocation));
        }
        return extracted;
    }

    private static Matrix4f aroundPivot(final Quaternionfc rotation,
                                        final double pivotX,
                                        final double pivotY,
                                        final double pivotZ) {
        return new Matrix4f()
                .translation((float) pivotX, (float) pivotY, (float) pivotZ)
                .rotate(rotation)
                .translate((float) -pivotX, (float) -pivotY, (float) -pivotZ);
    }

    private static Direction faceAfterTransform(final BakedQuad quad,
                                                final int[] transformedVertices,
                                                final Matrix4fc transform) {
        final Vec3 vertex0 = BakedQuadHelper.getXYZ(transformedVertices, 0);
        final Vec3 vertex1 = BakedQuadHelper.getXYZ(transformedVertices, 1);
        final Vec3 vertex2 = BakedQuadHelper.getXYZ(transformedVertices, 2);
        final Vector3f edgeA = new Vector3f(
                (float) (vertex1.x - vertex0.x),
                (float) (vertex1.y - vertex0.y),
                (float) (vertex1.z - vertex0.z)
        );
        final Vector3f edgeB = new Vector3f(
                (float) (vertex2.x - vertex1.x),
                (float) (vertex2.y - vertex1.y),
                (float) (vertex2.z - vertex1.z)
        );
        edgeA.cross(edgeB);
        if (edgeA.lengthSquared() <= NORMAL_EPSILON) {
            transform.transformDirection(
                    quad.getDirection().getStepX(),
                    quad.getDirection().getStepY(),
                    quad.getDirection().getStepZ(),
                    edgeA
            );
        }
        return Direction.getNearest(edgeA.x, edgeA.y, edgeA.z);
    }

    private static List<BakedQuad> map(final List<BakedQuad> quads, final Function<BakedQuad, BakedQuad> mapper) {
        final List<BakedQuad> mapped = new ArrayList<>(quads.size());
        for (final BakedQuad quad : quads) {
            mapped.add(mapper.apply(quad));
        }
        return mapped;
    }
}
