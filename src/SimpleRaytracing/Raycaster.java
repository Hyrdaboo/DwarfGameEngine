package SimpleRaytracing;

import DwarfEngine.Debug;
import DwarfEngine.MathTypes.Vector3;

public class Raycaster {
	private static float[] solveQuadratic(float a, float b, float c) {
		float discriminant = b*b-4*a*c;
		
		float[] solutions;
		if (discriminant > 0) {
			solutions = new float[2];
			float sqrtDiscr = (float) Math.sqrt(discriminant);
			solutions[0] = (-b - sqrtDiscr)/(2*a);
			solutions[1] = (-b + sqrtDiscr)/(2*a);
		}
		else if(discriminant == 0) {
			solutions = new float[1];
			solutions[0] = -b/(2*a);
		}
		else {
			solutions = null;
		}
		return solutions;
	}
	
	private static float[] evaluateQuadraticFormula(float coef1, float coef2) {
		float[] quadratic = new float[3];
		quadratic[0] = coef1*coef1;
		quadratic[1] = 2*coef1*coef2;
		quadratic[2] = coef2*coef2;
		return quadratic;
	}
	private static float[] addQuadraticFormulas(float[] quadratic1, float[] quadratic2) {
		float[] sum = new float[3];
		sum[0] = quadratic1[0]+quadratic2[0];
		sum[1] = quadratic1[1]+quadratic2[1];
		sum[2] = quadratic1[2]+quadratic2[2];
		return sum;
	}
	
	
	public static boolean Raycast(Vector3 origin, Vector3 direction, RaycastHit hit, Vector3 center, float radius) {
		if (hit == null) return false;
		
		float[] quadratic1 = evaluateQuadraticFormula(direction.x, origin.x-center.x);
		float[] quadratic2 = evaluateQuadraticFormula(direction.y, origin.y-center.y);
		float[] quadratic3 = evaluateQuadraticFormula(direction.z, origin.z-center.z);
		float[] finalQuadratic = addQuadraticFormulas(quadratic1, quadratic2);
		finalQuadratic = addQuadraticFormulas(finalQuadratic, quadratic3);
		
		float[] hitPoints = solveQuadratic(finalQuadratic[0], finalQuadratic[1], finalQuadratic[2]-(radius*radius));
		if (hitPoints == null) {
			return false;
		}
		Vector3 hitPoint = null;
		
		for (float f : hitPoints) {
			Vector3 p =  Vector3.add2Vecs(origin, Vector3.mulVecFloat(direction, f));
			Vector3 dirToHit = Vector3.subtract2Vecs(p, origin);
			float dot = Vector3.Dot(dirToHit.normalized(), p.normalized());
			if (dot > 0) {
				hitPoint = p;
				break;
			}
		}
		
		if (hitPoint == null) return false;
		
		hit.point = hitPoint;
		return true;
	}
	
	/*
	public static boolean Raycast(Vector3 origin, Vector3 direction, RaycastHit hit, Vector3 center, float radius) {
		if (hit == null) return false;
		
		float[] quadratic1 = evaluateQuadraticFormula(direction.x, origin.x-center.x);
		float[] quadratic2 = evaluateQuadraticFormula(direction.y, origin.y-center.y);
		float[] quadratic3 = evaluateQuadraticFormula(direction.z, origin.z-center.z);
		float[] finalQuadratic = addQuadraticFormulas(quadratic1, quadratic2);
		finalQuadratic = addQuadraticFormulas(finalQuadratic, quadratic3);
		
		float[] hitPoints = solveQuadratic(finalQuadratic[0], finalQuadratic[1], finalQuadratic[2]-(radius*radius));
		if (hitPoints == null) {
			return false;
		}
		Vector3 hitPoint = Vector3.add2Vecs(origin, Vector3.mulVecFloat(direction, hitPoints[0]));
		
		hit.point = hitPoint;
		return true;
	}
	*/
}
