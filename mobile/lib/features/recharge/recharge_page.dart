import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../core/theme/app_colors.dart';

class RechargePage extends StatelessWidget {
  const RechargePage({super.key});

  final _amounts = const [10, 30, 50, 100, 200, 500];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('充值')),
      body: SafeArea(
        child: Padding(
          padding: EdgeInsets.all(16.w),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                '选择充值金额',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              SizedBox(height: 16.h),
              Wrap(
                spacing: 12.w,
                runSpacing: 12.h,
                children: _amounts.map((amount) {
                  return _AmountChip(amount: amount);
                }).toList(),
              ),
              const Spacer(),
              ElevatedButton(
                onPressed: () {},
                child: const Text('立即充值'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _AmountChip extends StatefulWidget {
  final int amount;
  const _AmountChip({required this.amount});

  @override
  State<_AmountChip> createState() => _AmountChipState();
}

class _AmountChipState extends State<_AmountChip> {
  bool _selected = false;

  @override
  Widget build(BuildContext context) {
    return ChoiceChip(
      label: Text('¥${widget.amount}'),
      selected: _selected,
      onSelected: (selected) => setState(() => _selected = selected),
      selectedColor: AppColors.primary,
      labelStyle: TextStyle(
        color: _selected ? Colors.white : AppColors.textPrimary,
        fontWeight: FontWeight.w600,
      ),
    );
  }
}
