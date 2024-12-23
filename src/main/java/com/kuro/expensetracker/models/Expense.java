package com.kuro.expensetracker.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@DiscriminatorValue("expense")
public class Expense extends Transaction {

}
