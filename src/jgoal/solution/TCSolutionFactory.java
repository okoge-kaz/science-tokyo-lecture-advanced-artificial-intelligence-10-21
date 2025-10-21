package jgoal.solution;

import java.io.Serializable;

/**
 * 解テンプレートを利用するファクトリ．
 * cloneメソッドを生で利用した際に生じる型変換を隠蔽するためのユーティリティ．
 * @author isao
 *
 * @param <X>
 */
public class TCSolutionFactory<X extends ICSolution> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 解テンプレート */
	private X fSolutionTemplate;
	
	/**
	 * コンストラクタ
	 * @param solutionTemplate 解テンプレート
	 */
	public TCSolutionFactory(X solutionTemplate) {
		fSolutionTemplate = solutionTemplate;
	}
	
	/**
	 * 解を生成する．
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public X create() {
		return (X)fSolutionTemplate.clone();
	}
	
	/**
	 * 引数の解のコピーを生成する．
	 * @param src コピー元
	 * @return コピー
	 */
	@SuppressWarnings("unchecked")
	public X createClone(X src) {
		return (X)src.clone();
	}

}
