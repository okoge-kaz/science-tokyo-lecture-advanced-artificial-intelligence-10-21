package jssf.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * コンストラクタの引数を設定ファイルのキーと関連付ける．
 * このアノテーションはコンストラクタの引数に付加される．
 * すべての引数にこのアノテーションが付加されたコンストラクタをもつクラスは，
 * {@link TCObjectFactory#create(String)}などを用いてインスタンスの生成が可能となる．
 *
 * @since 2
 * @author hmkz
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ACParam {
	/**
	 * 変数名を返す
	 * @return 変数名
	 * @since 2 hmkz
	 */
	String key();

	/**
	 * デフォルト値を返す
	 * @return デフォルト値
	 * @since 2 hmkz
	 */
	String defaultValue() default "";

}
