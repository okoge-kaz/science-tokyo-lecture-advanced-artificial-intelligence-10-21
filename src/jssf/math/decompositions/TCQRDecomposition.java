package jssf.math.decompositions;
import jssf.math.TCMatrix;


/** 
 * QR分解クラス．
 * Jama.QRDecompositionを元に作成．
 * TCMatrix用に改変．
 * 再配布にはライセンス注意．
 * 20100822 取りあえず作成．コードをきれいにするのは後．
 * @author uemura
 * 
 * QR Decomposition.
 * <P>
 * For an m-by-n matrix A with m >= n, the QR decomposition is an m-by-n
 * orthogonal matrix Q and an n-by-n upper triangular matrix R so that
 * A = Q*R.
 * <P>
 * The QR decompostion always exists, even if the matrix does not have
 * full rank, so the constructor will never fail.  The primary use of the
 * QR decomposition is in the least squares solution of nonsquare systems
 * of simultaneous linear equations.  This will fail if isFullRank()
 * returns false.
 */

public class TCQRDecomposition implements java.io.Serializable {

	/* ------------------------
   Class variables
	 * ------------------------ */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Array for internal storage of decomposition.
   @serial internal array storage.
	 */
	private double[][] QR;

	/** Row and column dimensions.
   @serial column dimension.
   @serial row dimension.
	 */
	private int m, n;

	/** Array for internal storage of diagonal of R.
   @serial diagonal of R.
	 */
	private double[] Rdiag;

	/* ------------------------
   Constructor
	 * ------------------------ */

	/** QR Decomposition, computed by Householder reflections.
   @param A    Rectangular matrix
   @return     Structure to access R and the Householder vectors and compute Q.
	 */

	public TCQRDecomposition (TCMatrix A) {
		// Initialize.
//		QR = A.getArrayCopy();
		m = A.getRowDimension();
		n = A.getColumnDimension();
		QR = new double[m][n];
		for(int i=0; i<m; i++) {
			for(int j=0; j<n; j++) {
				QR[i][j] = A.getValue(i, j);
			}
		}
		Rdiag = new double[n];

		// Main loop.
		for (int k = 0; k < n; k++) {
			// Compute 2-norm of k-th column without under/overflow.
			double nrm = 0;
			for (int i = k; i < m; i++) {
				nrm = TCMaths.hypot(nrm,QR[i][k]);
			}

			if (nrm != 0.0) {
				// Form k-th Householder vector.
				if (QR[k][k] < 0) {
					nrm = -nrm;
				}
				for (int i = k; i < m; i++) {
					QR[i][k] /= nrm;
				}
				QR[k][k] += 1.0;

				// Apply transformation to remaining columns.
				for (int j = k+1; j < n; j++) {
					double s = 0.0; 
					for (int i = k; i < m; i++) {
						s += QR[i][k]*QR[i][j];
					}
					s = -s/QR[k][k];
					for (int i = k; i < m; i++) {
						QR[i][j] += s*QR[i][k];
					}
				}
			}
			Rdiag[k] = -nrm;
		}
	}

	/* ------------------------
   Public Methods
	 * ------------------------ */

	/** Is the matrix full rank?
   @return     true if R, and hence A, has full rank.
	 */

	public boolean isFullRank () {
		for (int j = 0; j < n; j++) {
			if (Rdiag[j] == 0)
				return false;
		}
		return true;
	}

	/** Return the Householder vectors
   @return     Lower trapezoidal matrix whose columns define the reflections
	 */

	public TCMatrix getH () {
		TCMatrix X = new TCMatrix(m, n);
//		Matrix X = new Matrix(m,n);
//		double[][] H = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (i >= j) {
					X.setValue(i, j, QR[i][j]);
					//H[i][j] = QR[i][j];
				} else {
					X.setValue(i, j, 0.0);
					//H[i][j] = 0.0;
				}
			}
		}
		return X;
	}

	/** Return the upper triangular factor
   @return     R
	 */

	public TCMatrix getR () {
		TCMatrix X = new TCMatrix(n, n);
		//Matrix X = new Matrix(n,n);
		//double[][] R = X.getArray();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i < j) {
					X.setValue(i, j, QR[i][j]);
					//R[i][j] = QR[i][j];
				} else if (i == j) {
					X.setValue(i, j, Rdiag[i]);
					//R[i][j] = Rdiag[i];
				} else {
					X.setValue(i, j, 0.0);
					//R[i][j] = 0.0;
				}
			}
		}
		return X;
	}

	/** Generate and return the (economy-sized) orthogonal factor
   @return     Q
	 */

	public TCMatrix getQ () {
		TCMatrix X = new TCMatrix(m, n);
		//Matrix X = new Matrix(m,n);
		//double[][] Q = X.getArray();
		for (int k = n-1; k >= 0; k--) {
			for (int i = 0; i < m; i++) {
				X.setValue(i, k, 0.0);
				//Q[i][k] = 0.0;
			}
			X.setValue(k, k, 1.0);
			//Q[k][k] = 1.0;
			for (int j = k; j < n; j++) {
				if (QR[k][k] != 0) {
					double s = 0.0;
					for (int i = k; i < m; i++) {
						s += QR[i][k] * X.getValue(i, j);
						//s += QR[i][k]*Q[i][j];
					}
					s = -s/QR[k][k];
					for (int i = k; i < m; i++) {
						X.setValue(i, j, X.getValue(i, j) + s*QR[i][k]);
						//Q[i][j] += s*QR[i][k];
					}
				}
			}
		}
		return X;
	}

	/** Least squares solution of A*X = B
   @param B    A Matrix with as many rows as A and any number of columns.
   @return     X that minimizes the two norm of Q*R*X-B.
   @exception  IllegalArgumentException  Matrix row dimensions must agree.
   @exception  RuntimeException  Matrix is rank deficient.
	 */

	public TCMatrix solve (TCMatrix B) {
		if (B.getRowDimension() != m) {
			throw new IllegalArgumentException("Matrix row dimensions must agree.");
		}
		if (!this.isFullRank()) {
			throw new RuntimeException("Matrix is rank deficient.");
		}

		// Copy right hand side
		int nx = B.getColumnDimension();
		double[][] X = new double[m][nx];
		for(int i=0; i<m; i++) {
			for(int j=0; j<nx; i++) {
				X[i][j] = B.getValue(i, j);
			}
		}
		//double[][] X = B.getArrayCopy();

		// Compute Y = transpose(Q)*B
		for (int k = 0; k < n; k++) {
			for (int j = 0; j < nx; j++) {
				double s = 0.0; 
				for (int i = k; i < m; i++) {
					s += QR[i][k]*X[i][j];
				}
				s = -s/QR[k][k];
				for (int i = k; i < m; i++) {
					X[i][j] += s*QR[i][k];
				}
			}
		}
		// Solve R*X = Y;
		for (int k = n-1; k >= 0; k--) {
			for (int j = 0; j < nx; j++) {
				X[k][j] /= Rdiag[k];
			}
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < nx; j++) {
					X[i][j] -= X[k][j]*QR[i][k];
				}
			}
		}
		//配列Xの部分配列X(0:n-1, 0:nx-1)で行列を作成．
		TCMatrix X2 = new TCMatrix(n, nx);
		for(int i=0; i<n; i++) {
			for(int j=0; j<nx; j++) {
				X2.setValue(i, j, X[i][j]);
			}
		}
		return X2;
//		return (new Matrix(X,n,nx).getMatrix(0,n-1,0,nx-1));
	}
}
