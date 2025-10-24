package com.briel.marnisos.brielapp.domain.models

data class PriceTablesModel(
    val companyName: String,
    val powerTerm: PowerTerm,
    val energyTerm: EnergyTerm,
) {
    companion object Companion {
        val empty = PriceTablesModel(
            companyName = "",
            powerTerm = PowerTerm(
                name = "",
                powerFeeList = emptyList()
            ),
            energyTerm = EnergyTerm(
                title = "",
                baseClassicPrice = BaseClassicPrice(
                    title = "",
                    fees = emptyList()
                ),
                uniqueClassicPrice = UniqueClassicPrice(
                    title = "",
                    fees = emptyList()
                )
            )
        )
    }
}

data class EnergyTerm(
    val title: String,
    val baseClassicPrice: BaseClassicPrice,
    val uniqueClassicPrice: UniqueClassicPrice,
)

data class BaseClassicPrice(
    val title: String,
    val fees: List<FeeModel>,
)

data class UniqueClassicPrice(
    val title: String,
    val fees: List<FeeModel>,
)

data class PowerTerm(
    val name: String,
    val powerFeeList: List<FeeModel>,
)

data class FeeModel(
    val feeName: String,
    val contractedPower: String? = null,
    val p1: Double? = null,
    val p2: Double? = null,
    val p3: Double? = null,
    val p4: Double? = null,
    val p5: Double? = null,
    val p6: Double? = null,
) {
    fun prices(): List<Double?> = listOf(p1, p2, p3, p4, p5, p6)
}
