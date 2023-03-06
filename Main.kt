package converter

import java.util.*

enum class UnitType {
    WEIGHT, LENGTH, TEMPERATURE, NULL
}

enum class Unit(val names: List<String>, val value: Double, val type: UnitType) {
    METER(listOf("m", "meter", "meters"), 1.0, UnitType.LENGTH),
    KILOMETER(listOf("km", "kilometer", "kilometers"), 1000.0, UnitType.LENGTH),
    CENTIMETER(listOf("cm", "centimeter", "centimeters"), 0.01, UnitType.LENGTH),
    MILLIMETER(listOf("mm", "millimeter", "millimeters"), 0.001, UnitType.LENGTH),
    MILE(listOf("mi", "mile", "miles"), 1609.35, UnitType.LENGTH),
    YARD(listOf("yd", "yard", "yards"), 0.9144, UnitType.LENGTH),
    FEET(listOf("ft", "foot", "feet"), 0.3048, UnitType.LENGTH),
    INCH(listOf("in", "inch", "inches"), 0.0254, UnitType.LENGTH),
    GRAM(listOf("g", "gram", "grams"), 1.0, UnitType.WEIGHT),
    KILOGRAM(listOf("kg", "kilogram", "kilograms"), 1000.0, UnitType.WEIGHT),
    MILLIGRAM(listOf("mg", "milligram", "milligrams"), 0.001, UnitType.WEIGHT),
    POUNDS(listOf("lb", "pound", "pounds"), 453.592, UnitType.WEIGHT),
    OUNCE(listOf("oz", "ounce", "ounces"), 28.3495, UnitType.WEIGHT),
    CELSIUS(listOf("celsius", "degree Celsius", "degrees Celsius", "dc", "c"), 0.0, UnitType.TEMPERATURE),
    FAHRENHEIT(listOf("fahrenheit", "degree Fahrenheit", "degrees Fahrenheit", "df", "f"), 0.0, UnitType.TEMPERATURE),
    KELVIN(listOf("k", "kelvin", "kelvins"), 0.0, UnitType.TEMPERATURE),
    NULL(listOf("", "", "???"), 0.0, UnitType.NULL)
}

fun getUnit(inputUnit: String): Unit {
    for (unit in Unit.values()) {
        if (inputUnit.lowercase() in unit.names.map { it.lowercase() }) return unit
    }
    return Unit.NULL
}

fun main() {
    println("""Instruction:
<input value> <input unit> %to%|%in% <unit to convert to>
ex.
1000 m in yard
12 kg to gram
12 degree Celsius convertTo fahrenheit

type exit to close the program

    """.trimMargin())
    while (true) {
        try {
            print("Enter what you want to convert (or exit): ")
            val input = readln().split(" ").toMutableList()
            if (input[0].lowercase() == "exit")
                break
            adjustInputIfFoundTemp(input, 1)
            adjustInputIfFoundTemp(input, 3)
            if (!(input[2].lowercase().contains("to") || input[2].lowercase().contains("in")))
                throw Exception()
            val inputValue = input[0].toDouble()
            val inputUnitStr = input[1]
            val outputUnitStr = input[3]
            val inputUnit: Unit = getUnit(inputUnitStr)
            val outputUnit = getUnit(outputUnitStr)
            if (UnitType.NULL in listOf(inputUnit.type, outputUnit.type) || inputUnit.type != outputUnit.type) {
                println("Conversion from ${inputUnit.names[2]} to ${outputUnit.names[2]} is impossible\n")
                continue
            }
            var outputValue: Double
            if (inputUnit.type in listOf(UnitType.LENGTH, UnitType.WEIGHT)) {
                if (inputValue < 0) {
                    println(
                        "${
                            inputUnit.type.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
                        } shouldn't be negative\n"
                    )
                    continue
                } else {
                    outputValue = inputUnit.value * inputValue / outputUnit.value
                }
            } else {
                outputValue = when {
                    inputUnit == outputUnit -> inputValue
                    inputUnit == Unit.CELSIUS && outputUnit == Unit.FAHRENHEIT -> inputValue * 9 / 5 + 32
                    inputUnit == Unit.FAHRENHEIT && outputUnit == Unit.CELSIUS -> (inputValue - 32) * 5 / 9

                    inputUnit == Unit.KELVIN && outputUnit == Unit.CELSIUS -> inputValue - 273.15
                    inputUnit == Unit.CELSIUS && outputUnit == Unit.KELVIN -> inputValue + 273.15
                    inputUnit == Unit.FAHRENHEIT && outputUnit == Unit.KELVIN -> (inputValue + 459.67) * 5 / 9

                    inputUnit == Unit.KELVIN && outputUnit == Unit.FAHRENHEIT -> inputValue * 9 / 5 - 459.67
                    else -> 0.0
                }
            }
            println(
                "$inputValue ${if (inputValue == 1.0) inputUnit.names[1] else inputUnit.names[2]} is " +
                        "$outputValue ${if (outputValue == 1.0) outputUnit.names[1] else outputUnit.names[2]}\n"
            )
        } catch (e: Exception) {
            println("Parse error\n")
        }
    }
}

fun adjustInputIfFoundTemp(input: MutableList<String>, index: Int) {
    if (input[index].lowercase() in listOf("degree", "degrees")) {
        input[index + 1] = "${input[index]} ${input[index + 1]}"
        input.removeAt(index)
    }
}
