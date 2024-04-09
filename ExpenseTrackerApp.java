import 'package:flutter/material.dart';

        void main() {
        runApp(ExpenseTrackerApp());
        }

class ExpenseTrackerApp extends StatelessWidget {
    @override
    Widget build(BuildContext context) {
        return MaterialApp(
                title: 'Expense Tracker',
                theme: ThemeData(
                primarySwatch: Colors.blue,
                visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
        home: ExpenseTrackerHomePage(),
    );
    }
}

class ExpenseTrackerHomePage extends StatefulWidget {
    @override
    _ExpenseTrackerHomePageState createState() => _ExpenseTrackerHomePageState();
}

class _ExpenseTrackerHomePageState extends State<ExpenseTrackerHomePage> {
    List<Transaction> _transactions = [];
    List<String> _categories = [
            'Food',
            'Shopping',
            'Transport',
            'Utilities',
            'Entertainment',
            'Miscellaneous'
            ];
    Map<String, double> _budgets = {
            'Food': 1000.0,
            'Shopping': 500.0,
            'Transport': 300.0,
            'Utilities': 200.0,
            'Entertainment': 400.0,
            'Miscellaneous': 0.0, // Initial budget for Miscellaneous category
};

  void _addTransaction(String title, double amount, String category, DateTime date) {
final newTransaction = Transaction(
        id: DateTime.now().toString(),
        title: title,
        amount: amount,
        category: category,
        date: date,
        );

        setState(() {
        _transactions.add(newTransaction);
        });
        }

        double get _totalSpent => _transactions.fold(0.0, (sum, item) => sum + item.amount);

        List<Transaction> get _recentTransactions => _transactions
        .where((tx) => tx.date.isAfter(DateTime.now().subtract(Duration(days: 7))))
        .toList();

        void _startAddNewTransaction(BuildContext ctx) {
        showModalBottomSheet(
        context: ctx,
        builder: (_) => GestureDetector(
        onTap: () {},
        child: TransactionForm(_addTransaction, _categories),
        behavior: HitTestBehavior.opaque,
        ),
        );
        }

@override
  Widget build(BuildContext context) {
          return Scaffold(
          appBar: AppBar(
          title: Text(
          'Expense Tracker',
          style: TextStyle(
          fontSize: 24.0,
          fontWeight: FontWeight.bold,
          ),
          ),
          actions: [
          IconButton(
          icon: Icon(Icons.add),
          onPressed: () => _startAddNewTransaction(context),
          ),
          ],
          ),
          body: SingleChildScrollView(
          child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
          _buildSummaryItem('Total Spent', '₹${_totalSpent.toStringAsFixed(2)}'),
          _buildSummaryItem('Transactions', '${_recentTransactions.length}'),
          _buildTitle('Expense Summaries'),
          ExpenseSummary(_transactions, _categories),
          SizedBox(height: 20),
          BudgetSummary(_transactions, _categories, _budgets),
          TransactionList(_transactions),
          ],
          ),
          ),
          floatingActionButton: FloatingActionButton(
          child: Icon(Icons.add),
          onPressed: () => _startAddNewTransaction(context),
          ),
          floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
          );
          }

          Widget _buildSummaryItem(String title, String value) {
          return Padding(
          padding: EdgeInsets.all(10),
          child: Card(
          elevation: 5,
          child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
          children: [
          Text(
          title,
          style: TextStyle(
          fontSize: 18.0,
          fontWeight: FontWeight.bold,
          ),
          ),
          SizedBox(height: 10),
          Text(
          value,
          style: TextStyle(
          fontWeight: FontWeight.bold,
          fontSize: 20,
          color: Colors.blue,
          ),
          ),
          ],
          ),
          ),
          ),
          );
          }

          Widget _buildTitle(String title) {
          return Padding(
          padding: EdgeInsets.all(10),
          child: Text(
          title,
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          textAlign: TextAlign.center,
          ),
          );
          }
          }

class Transaction {
    final String id;
    final String title;
    final double amount;
    final String category;
    final DateTime date;

    Transaction({
        required this.id,
                required this.title,
                required this.amount,
                required this.category,
                required this.date,
    });
}

class TransactionForm extends StatefulWidget {
    final Function addTransaction;
    final List<String> categories;

    TransactionForm(this.addTransaction, this.categories);

    @override
    _TransactionFormState createState() => _TransactionFormState();
}

class _TransactionFormState extends State<TransactionForm> {
    final _titleController = TextEditingController();
    final _amountController = TextEditingController();
    String? _selectedCategory;
    DateTime _selectedDate = DateTime.now();

    @override
    Widget build(BuildContext context) {
        return Card(
                elevation: 5,
                child: Padding(
                padding: EdgeInsets.all(10),
                child: Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: <Widget>[
        TextField(
                decoration: InputDecoration(labelText: 'Title'),
        controller: _titleController,
            ),
        TextField(
                decoration: InputDecoration(labelText: 'Amount'),
        controller: _amountController,
                keyboardType: TextInputType.numberWithOptions(decimal: true),
            ),
        DropdownButtonFormField<String>(
                value: _selectedCategory,
                onChanged: (newValue) {
                setState(() {
                _selectedCategory = newValue!;
                });
              },
        items: widget.categories.map((category) {
        return DropdownMenuItem<String>(
                value: category,
                child: Text(category),
                );
              }).toList(),
                hint: Text('Select Category'),
            ),
        Row(
                children: [
        Text('Date:'),
                SizedBox(width: 10),
        TextButton(
                onPressed: () => _presentDatePicker(context),
                child: Text(
                '${_selectedDate.day}/${_selectedDate.month}/${_selectedDate.year}',
                ),
                ),
              ],
            ),
        ElevatedButton(
                onPressed: _submitData,
                child: Text('Add Transaction'),
            ),
          ],
        ),
      ),
    );
    }

    void _submitData() {
        final enteredTitle = _titleController.text;
        final enteredAmount = double.tryParse(_amountController.text);

        if (enteredTitle.isEmpty || enteredAmount == null || _selectedCategory == null) {
            return;
        }

        widget.addTransaction(
                enteredTitle,
                enteredAmount,
                _selectedCategory!,
                _selectedDate,
    );

        Navigator.of(context).pop();
    }

    void _presentDatePicker(BuildContext context) {
        showDatePicker(
                context: context,
                initialDate: DateTime.now(),
                firstDate: DateTime(2021),
                lastDate: DateTime.now(),
    ).then((pickedDate) {
        if (pickedDate == null) {
            return;
        }
        setState(() {
            _selectedDate = pickedDate;
        });
    });
    }
}

class TransactionList extends StatelessWidget {
    final List<Transaction> transactions;

    TransactionList(this.transactions);

    @override
    Widget build(BuildContext context) {
        return Column(
                children: transactions.map((tx) {
        return Card(
                child: ListTile(
                leading: CircleAvatar(
                radius: 30,
                child: Padding(
                padding: EdgeInsets.all(6),
                child: FittedBox(
                child: Text('₹${tx.amount}'),
                ),
              ),
            ),
        title: Text(
                tx.title,
                style: TextStyle(fontWeight: FontWeight.bold),
            ),
        subtitle: Text(tx.category),
                trailing: Text('${tx.date.day}/${tx.date.month}/${tx.date.year}'),
          ),
        );
      }).toList(),
    );
    }
}

class ExpenseSummary extends StatelessWidget {
    final List<Transaction> transactions;
    final List<String> categories;

    ExpenseSummary(this.transactions, this.categories);

    @override
    Widget build(BuildContext context) {
        Map<String, double> categoryExpenses = {};

        transactions.forEach((transaction) {
                categoryExpenses[transaction.category] = (categoryExpenses[transaction.category] ?? 0) + transaction.amount;
    });

        return Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: categories.map((category) {
        return ListTile(
                title: Text(category),
                trailing: Text('₹${categoryExpenses[category]?.toStringAsFixed(2) ?? '0.00'}'),
        );
      }).toList(),
    );
    }
}

class BudgetSummary extends StatelessWidget {
    final List<Transaction> transactions;
    final List<String> categories;
    final Map<String, double> budgets;

    BudgetSummary(this.transactions, this.categories, this.budgets);

    @override
    Widget build(BuildContext context) {
        Map<String, double> categoryExpenses = {};

        transactions.forEach((transaction) {
                categoryExpenses[transaction.category] = (categoryExpenses[transaction.category] ?? 0) + transaction.amount;
    });

        return Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: categories.map((category) {
        final double monthlyBudget = budgets[category] ?? 0.0;
        final double totalSpent = categoryExpenses[category] ?? 0.0;

        return ListTile(
                title: Text(category),
                subtitle: Text('Monthly Budget: ₹$monthlyBudget\nTotal Spent: ₹$totalSpent'),
                trailing: totalSpent > monthlyBudget
                ? Text(
                'Overspent: ₹${(totalSpent - monthlyBudget).toStringAsFixed(2)}',
                style: TextStyle(color: Colors.red),
                )
              : null,
        );
      }).toList(),
    );
    }
}
