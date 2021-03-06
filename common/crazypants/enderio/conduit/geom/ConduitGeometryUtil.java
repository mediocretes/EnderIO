package crazypants.enderio.conduit.geom;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.render.BoundingBox;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector3d;

public class ConduitGeometryUtil {

  public static final ConduitGeometryUtil instance = new ConduitGeometryUtil();

  public static final float STUB_WIDTH = 0.2f;
  public static final float STUB_HEIGHT = 0.2f;

  public static final float WIDTH = 0.075f;
  public static final float HEIGHT = 0.075f;

  public static final float HWIDTH = WIDTH / 2;
  public static final float HHEIGHT = HEIGHT / 2;

  // All values are for a single conduit core
  public static final Vector3d CORE_MIN = new Vector3d(0.5f - HWIDTH, 0.5 - HHEIGHT, 0.5 - HWIDTH);
  public static final Vector3d CORE_MAX = new Vector3d(CORE_MIN.x + WIDTH, CORE_MIN.y + HEIGHT, CORE_MIN.z + WIDTH);
  public static final BoundingBox CORE_BOUNDS = new BoundingBox(CORE_MIN, CORE_MAX);

  private Map<GeometryKey, BoundingBox> boundsCache = new HashMap<GeometryKey, BoundingBox>();

  private EnumMap<ConduitConnectorType, BoundingBox> connectorBounds = new EnumMap<ConduitConnectorType, BoundingBox>(ConduitConnectorType.class);

  private ConduitGeometryUtil() {

  }

  public BoundingBox getBoundingBox(ConduitConnectorType type) {
    BoundingBox result = connectorBounds.get(type);
    if (result == null) {
      result = createConnector(type);
      result = result.scale(1.2f, 1.2f, 1.2f);
      connectorBounds.put(type, result);
    }
    return result;
  }

  private BoundingBox createConnector(ConduitConnectorType type) {
    float distance = WIDTH + HWIDTH;
    switch (type) {
    case VERTICAL:
      return new BoundingBox(0.5 - HWIDTH, 0.5 - distance, 0.5 - HWIDTH, 0.5 + HWIDTH, 0.5 + distance, 0.5 + HWIDTH);
    case HORIZONTAL:
      return new BoundingBox(0.5 - distance, 0.5 - HWIDTH, 0.5 - HWIDTH, 0.5 + distance, 0.5 + HWIDTH, 0.5 + HWIDTH);
    case BOTH:
      return createConnector(ConduitConnectorType.VERTICAL).expandBy(createConnector(ConduitConnectorType.HORIZONTAL));
    default:
      return CORE_BOUNDS;
    }

  }

  public BoundingBox getBoundingBox(Class<? extends IConduit> type, ForgeDirection dir, boolean isStub, Offset offset) {
    GeometryKey key = new GeometryKey(dir, isStub, offset, type);
    BoundingBox result = boundsCache.get(key);
    if (result == null) {
      result = createConduitBounds(type, key);
      boundsCache.put(key, result);
    }
    return result;
  }

  public Vector3d getTranslation(ForgeDirection dir, Offset offset) {
    Vector3d result = new Vector3d(offset.xOffset, offset.yOffset, 0);
    result.scale(WIDTH);
    return result;
  }

  private BoundingBox createConduitBounds(Class<? extends IConduit> type, GeometryKey key) {
    return createConduitBounds(type, key.dir, key.isStub, key.offset);
  }

  private BoundingBox createConduitBounds(Class<? extends IConduit> type, ForgeDirection dir, boolean isStub, Offset offset) {
    BoundingBox bb = CORE_BOUNDS;
    // if(type == IRedstoneConduit.class) {
    // bb = bb.scale(0.5f, 0.5f, 0.5f);
    // }
    Vector3d min = bb.getMin();
    Vector3d max = bb.getMax();

    switch (dir) {
    case WEST:
      min.x = isStub ? Math.max(0, bb.minX - STUB_WIDTH) : 0;
      max.x = bb.minX;
      break;
    case EAST:
      min.x = bb.maxX;
      max.x = isStub ? Math.min(1, bb.maxX + STUB_WIDTH) : 1;
      break;
    case DOWN:
      min.y = isStub ? Math.max(0, bb.minY - STUB_HEIGHT) : 0;
      max.y = bb.minY;
      break;
    case UP:
      max.y = isStub ? Math.min(1, bb.maxY + STUB_HEIGHT) : 1;
      min.y = bb.maxY;
      break;
    case NORTH:
      min.z = isStub ? Math.max(0.0F, bb.minZ - STUB_WIDTH) : 0;
      max.z = bb.minZ;
      break;
    case SOUTH:
      max.z = isStub ? Math.min(1F, bb.maxZ + STUB_WIDTH) : 1;
      min.z = bb.maxZ;
      break;
    default:
      break;
    }

    Vector3d trans = getTranslation(dir, offset);
    min.add(trans);
    max.add(trans);
    bb = new BoundingBox(VecmathUtil.clamp(min, 0, 1), VecmathUtil.clamp(max, 0, 1));
    return bb;
  }

}
