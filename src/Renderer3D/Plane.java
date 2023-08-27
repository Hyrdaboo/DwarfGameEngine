package Renderer3D;

import java.util.function.Function;

import DwarfEngine.MathTypes.Vector3;

class Plane {

	Vector3 point;
	Vector3 normal;

	public Plane(Vector3 point, Vector3 dir) {
		this.point = point;
		this.normal = dir;
	}

	private static float lineIntersectPlane(Vector3 planePoint, Vector3 planeNormal, Vector3 lineStart,
											Vector3 lineEnd) {
		planeNormal.Normalize();
		float planeD = -Vector3.Dot(planeNormal, planePoint);
		float ad = Vector3.Dot(lineStart, planeNormal);
		float bd = Vector3.Dot(lineEnd, planeNormal);
		return (-planeD - ad) / (bd - ad);
	}

	static Triangle[] triangleClipAgainstPlane(Vector3 planePoint, Vector3 planeNormal, Triangle inTri) {

		Triangle[] outTris = new Triangle[2];
		planeNormal.Normalize();

		Function<Vector3, Float> dist = (p) -> (planeNormal.x * p.x + planeNormal.y * p.y + planeNormal.z * p.z - Vector3.Dot(planeNormal, planePoint));

		Vertex[] insidePoints = new Vertex[3];
		int insidePointCount = 0;
		Vertex[] outsidePoints = new Vertex[3];
		int outsidePointCount = 0;

		float d0 = dist.apply(inTri.verts[0].position);
		float d1 = dist.apply(inTri.verts[1].position);
		float d2 = dist.apply(inTri.verts[2].position);

		if (d0 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[0];
			insidePointCount++;
		} else {
			outsidePoints[outsidePointCount] = inTri.verts[0];
			outsidePointCount++;
		}
		if (d1 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[1];
			insidePointCount++;
		} else {
			outsidePoints[outsidePointCount] = inTri.verts[1];
			outsidePointCount++;
		}
		if (d2 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[2];
			insidePointCount++;
		} else {
			outsidePoints[outsidePointCount] = inTri.verts[2];
		}

		if (insidePointCount == 3)
			outTris[0] = inTri;
		if (insidePointCount == 1) {
			outTris[0] = new Triangle();

			outTris[0].verts[0] = insidePoints[0];

			float intersection1 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0].position,
					outsidePoints[0].position);
			float intersection2 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0].position,
					outsidePoints[1].position);

			outTris[0].verts[1] = Vertex.Lerp(insidePoints[0], outsidePoints[0], intersection1);
			outTris[0].verts[2] = Vertex.Lerp(insidePoints[0], outsidePoints[1], intersection2);
		}
		if (insidePointCount == 2) {
			outTris[0] = new Triangle();
			outTris[1] = new Triangle();

			float intersection1 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0].position,
					outsidePoints[0].position);
			float intersection2 = lineIntersectPlane(planePoint, planeNormal, insidePoints[1].position,
					outsidePoints[0].position);

			outTris[0].verts[0] = insidePoints[0];
			outTris[0].verts[1] = insidePoints[1];
			outTris[0].verts[2] = Vertex.Lerp(insidePoints[0], outsidePoints[0], intersection1);

			outTris[1].verts[0] = insidePoints[1].clone();
			outTris[1].verts[1] = Vertex.Lerp(insidePoints[0], outsidePoints[0], intersection1);
			outTris[1].verts[2] = Vertex.Lerp(insidePoints[1], outsidePoints[0], intersection2);
		}

		return outTris;
	}
}
