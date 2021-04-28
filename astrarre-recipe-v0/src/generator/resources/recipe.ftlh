package io.github.astrarre.recipe.v0.api;

public interface Recipe {
	/**
	 * apply the recipe (does not check ifValid)
	 */
	void apply(Object... parameters);
	/**
	 * check if the current recipe is valid
	 */
	boolean isValid(Object... parameters);
	Object get(int index);
<#list prefixes as prefix>

	interface ${prefix}<<#list 0..prefix?index as val>${upperAlpha[val]}<#if val?is_last><#else>, </#if></#list>> {
		/**@see #apply(Object...)*/
		void apply(<#list 0..prefix?index as val>${upperAlpha[val]} ${lowerAlpha[val]}<#if val?is_last><#else>, </#if></#list>);
		/**@see #isValid(Object...)*/
		boolean isValid(<#list 0..prefix?index as val>${upperAlpha[val]} ${lowerAlpha[val]}<#if val?is_last><#else>, </#if></#list>);
		<#list 0..prefix?index as val>
		/**@see #get(int)*/
		${upperAlpha[val]} get${upperAlpha[val]}();
		</#list>
	}
</#list>
}
