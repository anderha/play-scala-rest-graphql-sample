package de.innfactory.todorestgraphqlsample.common.filteroptions

import dbdata.Tables
import de.innfactory.play.slick.enhanced.utils.filteroptions.{
  BooleanOption,
  FilterOptions,
  LongOption,
  OptionStringOption,
  StringOption
}

class FilterOptionsConfig {

  val todosFilterOptions: Seq[FilterOptions[Tables.Todo, _]] = Seq(
    BooleanOption(v => v.isDone, "isDone"),
    LongOption(v => v.createdAt, "createdAt")
  )

}
