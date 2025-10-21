package jssf.math;

import java.util.ArrayList;

/**
 * 主に行列演算系のユーティリティ．
 * 
 * @author uemura
 *
 */
public class TCMatrixUtility {
	
	/**
	 * n-by-1行列（ベクトル）の標準内積の計算
	 * @param v1 n-by-1 matrix
	 * @param v2 n-by-1 matrix
	 * @return 
	 */
	public static double innerProduct(TCMatrix v1, TCMatrix v2) {
		if(v1.getColumnDimension() != 1 || v2.getColumnDimension() != 1 || v1.getRowDimension() != v2.getRowDimension()) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		int d = v1.getRowDimension();
		double p = 0.0;
		for(int i=0; i<d; i++) {
			p += v1.getValue(i, 0) * v2.getValue(i, 0);
		}
		return p;
	}
	
	/**
	 * 区間[min, max]をn分割した座標を要素とするn次元の行ベクトル (1×n行列）を生成して返す．
	 * @param min 最小値
	 * @param max 最大値
	 * @param n 分割数
	 * @return n次元の行ベクトル (1×n行列）
	 * 	 
	 * @author isao
	 */
	public static TCMatrix linspace(double min, double max, int n) {
		double delta = (max - min) / (double)(n - 1);
		TCMatrix result = new TCMatrix(1, n);
		for (int i = 0; i < n - 1; ++i) {
			result.setValue(0, i, min + delta * (double)i);
		}
		result.setValue(0, n - 1, max);
		return result;
	}
	
	/**
	 * X成分のメッシュグリッド行列を生成して返す．主に3次元データプロットに利用される．
	 * @param x メッシュの交点のX座標を要素とするNx次元の行ベクトル
	 * @param y メッシュの交点のY座標を要素とするNy次元の行ベクトル
	 * @return Ny行Nx列のX成分メッシュグリッド行列
	 * 
	 * @author isao
	 */
	public static TCMatrix meshGridX(TCMatrix x, TCMatrix y) {
		TCMatrix xx = new TCMatrix(y.getColumnDimension(), x.getColumnDimension());
		for (int i = 0; i < xx.getRowDimension(); ++i) {
			for (int j = 0; j < xx.getColumnDimension(); ++j) {
				xx.setValue(i, j, x.getValue(j));
			}
		}
		return xx;
	}

	/**
	 * Y成分のメッシュグリッド行列を生成して返す．主に3次元データプロットに利用される．
	 * @param x メッシュの交点のX座標を要素とするNx次元の行ベクトル
	 * @param y メッシュの交点のY座標を要素とするNy次元の行ベクトル
	 * @return Ny行Nx列のY成分メッシュグリッド行列
	 * 
	 * @author isao
	 */
	public static TCMatrix meshGridY(TCMatrix x, TCMatrix y) {
		TCMatrix yy = new TCMatrix(y.getColumnDimension(), x.getColumnDimension());
		for (int i = 0; i < yy.getRowDimension(); ++i) {
			for (int j = 0; j < yy.getColumnDimension(); ++j) {
				yy.setValue(i, j, y.getValue(i));
			}
		}
		return yy;
	}
	
	/**
	 * サンプル点（列ベクトル）のリストとサンプル点の重心ベクトルから分散共分散行列を計算して返す．
	 * @param samples サンプル点（列ベクトル）のリスト
	 * @return 分散共分散行列
	 */
	public static TCMatrix calculateCovarianceMatrix(ArrayList<TCMatrix> samples) {
		int dim = samples.get(0).getDimension();
		TCMatrix mean = calculateMeanVector(samples);
		TCMatrix cov = new TCMatrix(dim, dim);
		for (TCMatrix x: samples) {
			for (int i = 0; i < dim; ++i) {
				for (int j = 0; j < dim; ++j) {
					double newValue = cov.getValue(i, j) + (x.getValue(i) - mean.getValue(i)) * (x.getValue(j) - mean.getValue(i));
					cov.setValue(i, j, newValue);
				}
			}
		}
		cov.div((double)samples.size());
		return cov;
	}
	
	/**
	 * サンプル点（列ベクトル）のリストから重心ベクトルを計算して返す．
	 * @param samples サンプル点（列ベクトル）のリスト
	 * @return 重心ベクトル
	 */
	public static TCMatrix calculateMeanVector(ArrayList<TCMatrix> samples) {
		int dim = samples.get(0).getDimension();
		TCMatrix mean = new TCMatrix(dim);
		for (TCMatrix x: samples) {
			mean.add(x);
		}
		mean.div((double)samples.size());
		return mean;
	}

}

